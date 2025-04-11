package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.AuthResponse;
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
    public AuthResponse loginWithGoogle(String idToken,
                                        HttpServletRequest request, HttpSession session) {
        try {
            GoogleIdToken token = googleTokenVerifier.verify(idToken);
            if (token == null) throw new AuthException("Invalid Google ID Token");

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String googleId = token.getPayload().getSubject();

            User user = getOrCreateGoogleUser(payload, googleId);

            String userAgent = request.getHeader("User-Agent");
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            Browser browser = ua.getBrowser();
            OperatingSystem os = ua.getOperatingSystem();

            Optional<DeviceInfo> deviceOpt = deviceInfoRepository.findByDeviceIdAndUser(session.getId(), user);
            if (deviceOpt.isPresent()) {
                if (!isDeviceInUse(user, deviceOpt.get())) {
                    sendDeviceNotification(user, deviceOpt.get());
                    throw new AuthException("OTP verification required");
                }
            } else {
                DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
                sendFirstLoginNotification(user, newDevice);
                throw new AuthException("OTP verification required");
            }

            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);
            return new AuthResponse(email, accessToken, refreshToken);
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

    public boolean saveDeviceAfterOtp(String otp, HttpServletRequest request,
                                      HttpSession session, String userId) {
        UserOtp userOtp = userOtpRepository.findByUserId(userId);
        if (userOtp == null || userOtp.getTimeEnd().isBefore(LocalDateTime.now())) return false;

        if (!otp.equals(userOtp.getOtpCode())) return false;

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        UserAgent ua = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        DeviceInfo device = createDeviceInfo(session, ua.getBrowser(), ua.getOperatingSystem(), user, request);
        deviceInfoRepository.save(device);
        return true;
    }

    private DeviceInfo createDeviceInfo(HttpSession session, Browser browser,
                                        OperatingSystem os, User user, HttpServletRequest request) {
        DeviceInfo device = new DeviceInfo();
        device.setDeviceId(session.getId());
        device.setDeviceName(os.getName());
        device.setOs(os.getName());
        device.setBrowser(browser.getName());
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
