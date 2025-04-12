package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.AuthResponse;
import com.cryptobank.backend.DTO.UserAuthResponse;
import com.cryptobank.backend.entity.*;
import com.cryptobank.backend.exception.AuthException;
import com.cryptobank.backend.repository.*;
import com.cryptobank.backend.utils.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDAO userRepository;
    private final DeviceInforDAO deviceInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserOtpRepository userOtpRepository;
    private final GoogleAuthRepository googleAuthRepository;
    private final EmailDeviceService emailDeviceService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final GoogleIdTokenVerifier googleTokenVerifier;
    private UserService userService;

    public JwtUtil getJwtUtil() {
        return jwtUtil;
    }

    // ---------------- LOGIN with Email/Password ------------------
    public AuthResponse loginWithEmail(String email, String password, boolean rememberMe,
                                       HttpServletRequest request, HttpSession session) {
        try {
            int loginResult = handleLogin(email, password, request, session);
            User user = userRepository.findByEmail(email);

            if (loginResult == 0) {
                String accessToken = jwtUtil.generateAccessToken(email);
                String refreshToken = rememberMe
                        ? jwtUtil.generateRefreshToken(email, 30 * 24 * 60 * 60)
                        : jwtUtil.generateRefreshToken(email);
                return new AuthResponse(email, accessToken, refreshToken);
            } else if (loginResult == 1) {
                throw new AuthException("OTP verification required");
            } else {
                throw new AuthException("Invalid credentials");
            }
        } catch (Exception e) {
            throw new AuthException("Login failed: " + e.getMessage());
        }
    }

    private int handleLogin(String email, String password,
                            HttpServletRequest request, HttpSession session) {
        User user = userRepository.findByEmail(email);
        System.out.println(passwordEncoder.encode("abc123"));
        System.out.println(passwordEncoder.encode(password));
        System.out.println(passwordEncoder.encode(user.getPassword()));
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return 2; 
        }

        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        OperatingSystem os = ua.getOperatingSystem();
        Browser browser = ua.getBrowser();

        Optional<DeviceInfo> deviceOpt = deviceInfoRepository.findByDeviceIdAndUser(session.getId(), user);
        if (deviceOpt.isPresent()) {
            if (isDeviceInUse(user, deviceOpt.get())) {
                return 0;
            } else {
                sendDeviceNotification(user, deviceOpt.get());
                return 1;
            }
        } else {
            DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
            sendFirstLoginNotification(user, newDevice);
            return 1;
        }
    }

    // ---------------- LOGIN with Google ------------------
    public UserAuthResponse loginWithGoogle(String idToken, boolean rememberMe, HttpServletRequest request, HttpSession session) {
        try {
            GoogleIdToken token = googleTokenVerifier.verify(idToken);
            if (token == null) throw new AuthException("Invalid Google ID Token");

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String googleId = token.getPayload().getSubject();

            User user = getOrCreateGoogleUser(payload, googleId);
            user.setLastLoginAt(OffsetDateTime.now());
            userRepository.save(user);

            String userAgent = request.getHeader("User-Agent");
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            Browser browser = ua.getBrowser();
            OperatingSystem os = ua.getOperatingSystem();

            Optional<DeviceInfo> deviceOpt = deviceInfoRepository.findByDeviceIdAndUser(session.getId(), user);
            if (deviceOpt.isPresent()) {
                if (!isDeviceInUse(user, deviceOpt.get())) {
                    sendDeviceNotification(user, deviceOpt.get());
                    throw new AuthException("OTP verification required");
                } else {
                    DeviceInfo device = deviceOpt.get();
                    device.setLastLoginAt(OffsetDateTime.now());
                    deviceInfoRepository.save(device);
                }
            } else {
                DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
                sendFirstLoginNotification(user, newDevice);
                throw new AuthException("OTP verification required");
            }

            String role = userService.getUserRole(user.getId())
                .map(userRole -> userRole.getRole().getName())
                .orElse("USER");

            UserAuthResponse response = new UserAuthResponse();
            response.setId(user.getId());
            response.setRole(role);
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setUsername(user.getUsername());
            response.setPassword(user.getPassword());
            response.setAvatar(user.getAvatar());
            response.setKycStatus(user.getKycStatus());
            response.setWalletAddress(user.getWalletAddress());
            response.setFirstName(user.getFirstName());
            response.setRememberMe(rememberMe);

            return response;
        } catch (Exception e) {
            throw new AuthException("Google login failed: " + e.getMessage());
        }
    }

    private User getOrCreateGoogleUser(GoogleIdToken.Payload payload, String googleId) {
        Optional<GoogleAuth> googleAuth = googleAuthRepository.findByGoogleId(googleId);
        if (googleAuth.isPresent()) return googleAuth.get().getUser();

        String email = payload.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName((String) payload.get("given_name"));
            user.setLastName((String) payload.get("family_name"));
            user.setPassword(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(10)));
            user = userRepository.save(user);
        }

        GoogleAuth newAuth = new GoogleAuth();
        newAuth.setGoogleId(googleId);
        newAuth.setUser(user);
        googleAuthRepository.save(newAuth);
        return user;
    }

    // ------------------ OTP + Device Handler -----------------------
    private boolean isDeviceInUse(User user, DeviceInfo device) {
        return deviceInfoRepository.findByUserAndDeviceInUse(user.getId())
                .map(d -> d.getDeviceId().equals(device.getDeviceId()))
                .orElse(false);
    }

    public boolean saveDeviceAfterOtp(String otp, HttpServletRequest request, HttpSession session, String userId) {
        UserOtp userOtp = userOtpRepository.findByUserId(userId);
        if (userOtp == null || userOtp.getTimeEnd().isBefore(LocalDateTime.now())) {
            System.out.println("OTP invalid or expired for userId: " + userId);
            return false;
        }

        if (!otp.equals(userOtp.getOtpCode())) {
            System.out.println("OTP mismatch for userId: " + userId);
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            System.out.println("User not found for userId: " + userId);
            return false;
        }

        UserAgent ua = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        DeviceInfo device = createDeviceInfo(session, ua.getBrowser(), ua.getOperatingSystem(), user, request);
        try {
            deviceInfoRepository.save(device);
            System.out.println("Device saved for userId: " + userId + ", deviceId: " + device.getDeviceId());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to save device for userId: " + userId + ": " + e.getMessage());
            return false;
        }
    }

    private DeviceInfo createDeviceInfo(HttpSession session, Browser browser, OperatingSystem os, User user, HttpServletRequest request) {
        DeviceInfo device = new DeviceInfo();
        device.setDeviceId(session.getId());
        device.setDeviceName(os != null && !os.getName().equals("Unknown") ? os.getName() : "Unknown Device");
        device.setOs(os != null && !os.getName().equals("Unknown") ? os.getName() : "Unknown OS");
        device.setBrowser(browser != null && !browser.getName().equals("Unknown") ? browser.getName() : "Unknown Browser");
        device.setIpAddress(request.getRemoteAddr());
        device.setLastLoginAt(OffsetDateTime.now());
        device.setUser(user);
        device.setInUse(true);
        return device;
    }
    private String createOtp(User user) {
        int otpCode = 100000 + new Random().nextInt(900000);
        UserOtp userOtp = Optional.ofNullable(userOtpRepository.findByUserId(user.getId()))
                .orElse(new UserOtp());
        if (userOtp == null) {
            userOtp = new UserOtp();
            userOtp.randomId(); // ✅ Chỉ gọi khi tạo mới
        }
        
        userOtp.setUser(user);
        userOtp.setOtpCode(String.valueOf(otpCode));
        userOtp.setTimeStart(LocalDateTime.now());
        userOtp.setTimeEnd(LocalDateTime.now().plusMinutes(2));
        userOtpRepository.save(userOtp);

        return String.valueOf(otpCode);
    }

    private void sendDeviceNotification(User user, DeviceInfo device) {
        String otp = createOtp(user);
        String subject = "Đăng nhập từ thiết bị lạ!";
        String message = buildDeviceMessage(user, device, otp, false);
        emailDeviceService.sendEmail(user.getEmail(), subject, message);
    }

    private void sendFirstLoginNotification(User user, DeviceInfo device) {
        String otp = createOtp(user);
        String subject = "Lần đầu đăng nhập!";
        String message = buildDeviceMessage(user, device, otp, true);
        emailDeviceService.sendEmail(user.getEmail(), subject, message);
    }

    private String buildDeviceMessage(User user, DeviceInfo device, String otp, boolean isFirstLogin) {
        String title = isFirstLogin ? "Chúng tôi nhận thấy bạn là người dùng mới" :
                "Tài khoản của bạn đang đăng nhập từ thiết bị khác";

        return String.format("""
                Xin chào %s,

                %s:

                🔹 Thiết bị: %s
                🔹 Hệ điều hành: %s
                🔹 Trình duyệt: %s
                🔹 IP: %s
                🔹 Thời gian: %s

                🔹 Mã OTP (hiệu lực 2 phút): %s

                Nếu không phải bạn, vui lòng đổi mật khẩu ngay!
                """,
                user.getFirstName() + " " + user.getLastName(),
                title,
                device.getDeviceName(),
                device.getOs(),
                device.getBrowser(),
                device.getIpAddress(),
                device.getLastLoginAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                otp
        );
    }

    // ------------------ Basic Auth + Logout -----------------------
    public void logout(HttpSession session) {
        session.invalidate();
    }

    public AuthResponse authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return new AuthResponse(
                username,
                jwtUtil.generateAccessToken(username),
                jwtUtil.generateRefreshToken(username)
        );
    }

    public List<Optional<DeviceInfo>> getAllDeviceFromUser(String userId) {
        return deviceInfoRepository.getAllDeviceWasLoginByUser(userId);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
