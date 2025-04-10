package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.AuthResponse;
import com.cryptobank.backend.entity.*;
import com.cryptobank.backend.exception.AuthException;
import com.cryptobank.backend.repository.*;
import com.cryptobank.backend.utils.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDAO userRepository;
    private final DeviceInforDAO deviceInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserOtpRepository userOtpRepository;
    private final GoogleAuthRepository googleAuthRepository;
    private final EmailDeviceService emailDeviceService; // Đảm bảo là bean
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final GoogleIdTokenVerifier googleTokenVerifier;

    // Getter for JwtUtil
    public JwtUtil getJwtUtil() {
        return jwtUtil;
    }

    public List<Optional<DeviceInfo>> getAllDeviceFromUser(String userId) {
        return deviceInfoRepository.getAllDeviceWasLoginByUser(userId);
    }

    // Login với email/password và Remember Me
    public AuthResponse loginWithEmail(String email, String password, boolean rememberMe, 
                                       HttpServletRequest request, HttpSession session) {
        try {
            int result = login(email, password, request, session);
            User user = userRepository.findByEmail(email);

            if (result == 0) {
                String accessToken = jwtUtil.generateAccessToken(email);
                String refreshToken = rememberMe ? 
                    jwtUtil.generateRefreshToken(email, 30 * 24 * 60 * 60) : 
                    jwtUtil.generateRefreshToken(email);
                return new AuthResponse(email, accessToken, refreshToken);
            } else if (result == 1) {
                throw new AuthException("OTP verification required");
            } else {
                throw new AuthException("Invalid credentials");
            }
        } catch (Exception e) {
            throw new AuthException("Login failed: " + e.getMessage());
        }
    }

    // Login với Google
    public AuthResponse loginWithGoogle(String idToken, HttpServletRequest request, HttpSession session) {
        try {
            GoogleIdToken googleIdToken = googleTokenVerifier.verify(idToken);
            if (googleIdToken == null) {
                throw new AuthException("Invalid Google ID Token");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String googleId = payload.getSubject();

            Optional<GoogleAuth> googleAuthOpt = googleAuthRepository.findByGoogleId(googleId);
            User user;

            if (googleAuthOpt.isPresent()) {
                user = googleAuthOpt.get().getUser();
            } else {
                user = userRepository.findByEmail(email);
                if (user == null) {
                    user = new User();
                    user.setEmail(email);
                    user.setFirstName((String) payload.get("given_name"));
                    user.setLastName((String) payload.get("family_name"));
                    user.setPassword(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(10)));
                    user = userRepository.save(user);
                }

                GoogleAuth googleAuth = new GoogleAuth();
                googleAuth.setGoogleId(googleId);
                googleAuth.setUser(user);
                googleAuthRepository.save(googleAuth);
            }

            String userAgent = request.getHeader("User-Agent");
            Parser parser = new Parser();
            Client client = parser.parse(userAgent);
            Optional<DeviceInfo> existingDevice = deviceInfoRepository.findByDeviceIdAndUser(session.getId(), user);

            if (existingDevice.isPresent()) {
                DeviceInfo device = existingDevice.get();
                if (!checkDevicePresentIsInUse(user, device)) {
                    noticationDifferentDeviceLogin(user, device);
                    throw new AuthException("OTP verification required");
                }
            } else {
                DeviceInfo newDevice = formatToDeviceInfor(session, client, user, request);
                firstDeviceLoginNotication(user, newDevice);
                throw new AuthException("OTP verification required");
            }

            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);
            return new AuthResponse(email, accessToken, refreshToken);

        } catch (Exception e) {
            throw new AuthException("Google login failed: " + e.getMessage());
        }
    }

    public int login(String email, String password, HttpServletRequest request, HttpSession session) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return 2; // User không tồn tại
            }

            if (passwordEncoder.matches(password, user.getPassword())) {
                String userAgent = request.getHeader("User-Agent");
                Parser parser = new Parser();
                Client client = parser.parse(userAgent);
                Optional<DeviceInfo> existingDevice = deviceInfoRepository.findByDeviceIdAndUser(session.getId(), user);

                if (existingDevice.isPresent()) {
                    DeviceInfo device = existingDevice.get();
                    if (checkDevicePresentIsInUse(user, device)) {
                        return 0; // Thiết bị đã xác thực
                    } else {
                        noticationDifferentDeviceLogin(user, device);
                        return 1; // Cần OTP
                    }
                } else {
                    DeviceInfo newDevice = formatToDeviceInfor(session, client, user, request);
                    firstDeviceLoginNotication(user, newDevice);
                    return 1; // Cần OTP
                }
            } else {
                return 2; // Sai mật khẩu
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    private void sendNewDeviceAlert(User user, DeviceInfo device, Boolean checkContaint) {
        if (!checkContaint) {
            String subject = "Cảnh báo đăng nhập từ thiết bị mới!";
            String message = String.format(
                    "Xin chào %s,\n\n" +
                    "Chúng tôi phát hiện tài khoản của bạn vừa đăng nhập từ một thiết bị mới:\n\n" +
                    "🔹 Thiết bị: %s\n" +
                    "🔹 Hệ điều hành: %s\n" +
                    "🔹 Trình duyệt: %s\n" +
                    "🔹 Địa chỉ IP: %s\n" +
                    "🔹 Thời gian đăng nhập: %s\n\n" +
                    "Nếu đây không phải bạn, vui lòng đổi mật khẩu ngay lập tức hoặc liên hệ hỗ trợ!",
                    user.getFirstName() + " " + user.getLastName(),
                    device.getDeviceName(), device.getOs(), device.getBrowser(), device.getIpAddress(),
                    device.getLastLoginAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            emailDeviceService.sendEmail(user.getEmail(), subject, message);
        }
    }

    private void firstDeviceLoginNotication(User user, DeviceInfo device) {
        String otp = CreateOTP(user);
        String subject = "Thông báo lần đầu đăng nhập!";
        String message = String.format(
                "Xin chào %s,\n\n" +
                "Chúng tôi nhận thấy tài khoản của bạn là người dùng mới:\n\n" +
                "🔹 Thiết bị: %s\n" +
                "🔹 Hệ điều hành: %s\n" +
                "🔹 Trình duyệt: %s\n" +
                "🔹 Địa chỉ IP: %s\n" +
                "🔹 Thời gian đăng nhập: %s\n\n" +
                "🔹 Đây là mã xác thực OTP cho lần đầu đăng nhập (có hiệu lực 2 phút): %s\n\n" +
                "Vui lòng dùng mã xác thực để trải nghiệm CryptoBank!",
                user.getFirstName() + " " + user.getLastName(),
                device.getDeviceName(), device.getOs(), device.getBrowser(), device.getIpAddress(),
                device.getLastLoginAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                otp);
        emailDeviceService.sendEmail(user.getEmail(), subject, message);
    }

    private void noticationDifferentDeviceLogin(User user, DeviceInfo device) {
        String otp = CreateOTP(user);
        String subject = "Thông báo đăng nhập trên thiết bị lạ!";
        String message = String.format(
                "Xin chào %s,\n\n" +
                "Chúng tôi nhận thấy tài khoản của bạn đang đăng nhập trên thiết bị khác:\n\n" +
                "🔹 Thiết bị: %s\n" +
                "🔹 Hệ điều hành: %s\n" +
                "🔹 Trình duyệt: %s\n" +
                "🔹 Địa chỉ IP: %s\n" +
                "🔹 Thời gian đăng nhập: %s\n\n" +
                "🔹 Đây là mã xác thực OTP (có hiệu lực 2 phút): %s\n\n" +
                "Nếu đây không phải bạn, vui lòng đăng nhập sau đó thay đổi mật khẩu!",
                user.getFirstName() + " " + user.getLastName(),
                device.getDeviceName(), device.getOs(), device.getBrowser(), device.getIpAddress(),
                device.getLastLoginAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                otp);
        emailDeviceService.sendEmail(user.getEmail(), subject, message);
    }

    private Boolean checkDevicePresentIsInUse(User user, DeviceInfo device) {
        Optional<DeviceInfo> deviceInUse = deviceInfoRepository.findByUserAndDeviceInUse(user.getId());
        return deviceInUse.isPresent() && device.getDeviceId().equals(deviceInUse.get().getDeviceId());
    }

    private String CreateOTP(User user) {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        UserOtp userOtp = userOtpRepository.findByUserId(user.getId()).orElse(new UserOtp());
        if (userOtp.getId() == null) {
            userOtp.setUser(user);
            userOtp.randomId();
        }
        userOtp.setOtpCode(String.valueOf(otp));
        userOtp.setTimeStart(LocalDateTime.now());
        userOtp.setTimeEnd(LocalDateTime.now().plusMinutes(2));
        userOtpRepository.save(userOtp);
        return String.valueOf(otp);
    }

    public Boolean saveDeviceInforToDB(String OTPFromUser, HttpServletRequest request, HttpSession session, String userId) {
        UserOtp userOtp = userOtpRepository.findByUserId(userId).orElse(null);
        if (userOtp == null || userOtp.getTimeEnd().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (OTPFromUser.equals(userOtp.getOtpCode())) {
            String userAgent = request.getHeader("User-Agent");
            Parser parser = new Parser();
            Client client = parser.parse(userAgent);
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                DeviceInfo newDevice = formatToDeviceInfor(session, client, user, request);
                deviceInfoRepository.save(newDevice);
                return true;
            }
            return false;
        }
        return false;
    }

    private DeviceInfo formatToDeviceInfor(HttpSession session, Client client, User user, HttpServletRequest request) {
        DeviceInfo newDevice = new DeviceInfo();
        newDevice.setDeviceId(session.getId());
        newDevice.setDeviceName(client.device.family);
        newDevice.setOs(client.os.family + " " + client.os.major);
        newDevice.setBrowser(client.userAgent.family + " " + client.userAgent.major);
        newDevice.setIpAddress(request.getRemoteAddr());
        newDevice.setLastLoginAt(OffsetDateTime.now());
        newDevice.setUser(user);
        newDevice.setInUse(true);
        return newDevice;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public AuthResponse authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username); // Sửa từ password thành username
        return new AuthResponse(username, accessToken, refreshToken);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}