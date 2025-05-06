package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.AuthResponse;
import com.cryptobank.backend.DTO.AuthenticationResponse;
import com.cryptobank.backend.DTO.UserAuthResponse;
import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.entity.*;
import com.cryptobank.backend.exception.AuthException;
import com.cryptobank.backend.repository.*;
import com.cryptobank.backend.utils.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDAO userRepository;
    private final DebitWalletDAO debitWalletRepository;
    private final DeviceInforDAO deviceInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserOtpRepository userOtpRepository;
    private final GoogleAuthRepository googleAuthRepository;
    private final EmailDeviceService emailDeviceService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final GoogleIdTokenVerifier googleTokenVerifier;
    private final UserService userService;
    private final EntityManager entityManager;

    // ---------------- LOGIN with Email/Password ------------------
    public UserAuthResponse loginWithEmail(String email, String password, boolean rememberMe, HttpServletRequest request,
                                           HttpSession session) {
        try {
            int loginResult = handleLogin(email, password, request, session);
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new AuthException("User not found");
            }

            if (loginResult == 0) {
                String role = userService.getUserRole(user.getId())
                        .map(userRole -> userRole.getRole().getName())
                        .orElse("USER");
                return buildUserAuthResponse(user, role, rememberMe);
            } else if (loginResult == 1) {
                throw new AuthException("OTP verification required", user.getId());
            } else {
                throw new AuthException("Invalid Email or Password");
            }
        } catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

    private int handleLogin(String email, String password, HttpServletRequest request, HttpSession session) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthException("Invalid Email");
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid Password");
        }

        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        Browser browser = ua.getBrowser();
        OperatingSystem os = ua.getOperatingSystem();

        DeviceInfo currentDevice = createDeviceInfo(session, browser, os, user, request);
        String currentBrowser = currentDevice.getBrowser();
        String currentOs = currentDevice.getOs();
        String currentDeviceName = currentDevice.getDeviceName();

        Optional<DeviceInfo> activeDeviceOpt = deviceInfoRepository.findByUserAndDeviceInUse(user.getId());
        if (activeDeviceOpt.isPresent()) {
            DeviceInfo activeDevice = activeDeviceOpt.get();
            boolean isSameDevice = activeDevice.getBrowser().equals(currentBrowser) &&
                    activeDevice.getOs().equals(currentOs) &&
                    activeDevice.getDeviceName().equals(currentDeviceName);
            if (isSameDevice) {
                activeDevice.setLastLoginAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
                deviceInfoRepository.save(activeDevice);
                return 0;
            } else {
                Optional<DeviceInfo> deviceOpt = deviceInfoRepository.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs,user.getId());
                if (deviceOpt.isPresent()) {
                    sendDeviceNotification(user, deviceOpt.get());
                    return 1;
                } else {
                    DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
                    deviceInfoRepository.save(newDevice);
                    sendFirstLoginNotification(user, newDevice);
                    return 1;
                }
            }
        } else {
            DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
            deviceInfoRepository.save(newDevice);
            sendFirstLoginNotification(user, newDevice);
            return 1;
        }
    }

    // ---------------- LOGIN with Google ------------------
    @Transactional(rollbackOn = Exception.class, dontRollbackOn  = AuthException.class)
    public  Map<String, Object> loginWithGoogle(String idToken, boolean rememberMe, HttpServletRequest request,
                                            HttpSession session) {
        try {
            GoogleIdToken token = googleTokenVerifier.verify(idToken);
            if (token == null) {
                throw new AuthException("Invalid Google ID Token !!");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String googleId = token.getPayload().getSubject();

            User user = getOrCreateGoogleUser(payload, googleId); // Sửa lỗi: thay 'angiosperm' bằng 'getOrCreateGoogleUser'
            user.setLastLoginAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            user = userRepository.save(user);
            entityManager.flush();
            entityManager.refresh(user);
            System.out.println("User saved, userId: " + user.getId());
            if (user.getId() == null) {
                System.err.println("Error: userId is null after saving user");
                throw new IllegalStateException("User ID is null after saving");
            }

            String userAgent = request.getHeader("User-Agent");
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            Browser browser = ua.getBrowser();
            OperatingSystem os = ua.getOperatingSystem();

            DeviceInfo currentDevice = createDeviceInfo(session, browser, os, user, request);
            String currentBrowser = currentDevice.getBrowser();
            String currentOs = currentDevice.getOs();
            String currentDeviceName = currentDevice.getDeviceName();

            Optional<DeviceInfo> deviceOpt = deviceInfoRepository.findByInforOfDevice(currentDeviceName, currentBrowser,currentOs,user.getId());
            if (deviceOpt.isPresent()) {
                if (!isDeviceInUse(user, deviceOpt.get())) {
                    System.out.println("User ID before throwing exception (existing device): " + user.getId());
                    sendDeviceNotification(user, deviceOpt.get());
                    session.setAttribute("otpUserId", user.getId());
                    throw new AuthException("OTP verification required", user.getId());
                } else {
                    DeviceInfo device = deviceOpt.get();
                    device.setLastLoginAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
                    deviceInfoRepository.save(device);
                    
                }
            } else {
                DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
                deviceInfoRepository.save(newDevice);
                System.out.println("User ID before throwing exception (new device): " + user.getId());
                sendFirstLoginNotification(user, newDevice);
                session.setAttribute("otpUserId", user.getId());
                throw new AuthException("OTP verification required", user.getId());
            }

            String role = userService.getUserRole(user.getId())
                    .map(userRole -> userRole.getRole().getName())
                    .orElse("USER");
            UserInformation userInformation = userService.convertToUserInformation(user);
            String accessToken = jwtUtil.generateToken(userInformation, 1000 * 60 * 30);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("userData", buildUserAuthResponse(user, role, rememberMe));
            
            return response;
        } catch (Exception e) {
            throw new AuthException("Google login failed: " + e.getMessage());
        }
    }

    private User getOrCreateGoogleUser(GoogleIdToken.Payload payload, String googleId) {
        Optional<GoogleAuth> googleAuth = googleAuthRepository.findByGoogleId(googleId);
        if (googleAuth.isPresent()) {
            User user = googleAuth.get().getUser();
            System.out.println("Found existing GoogleAuth, userId: " + user.getId());
            return user;
        }

        String email = payload.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName((String) payload.get("given_name"));
            user.setLastName((String) payload.get("family_name"));
            user.setFullName((String) payload.get("name"));
            user.setUsername(email.split("@")[0]);
            user.setAvatar((String) payload.get("picture"));
            user.setPassword(passwordEncoder.encode("123456789"));
            user.setIsBankAccount(false);
            user.setCreatedAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        }

        user = userRepository.save(user);
        entityManager.flush();
        entityManager.refresh(user);
        System.out.println("User saved in getOrCreateGoogleUser, userId: " + user.getId());

        GoogleAuth newAuth = new GoogleAuth();
        newAuth.setGoogleId(googleId);
        newAuth.setUser(user);
        googleAuthRepository.save(newAuth);

        createDebitAccount(user);

        return user;
    }

    // ------------------ OTP + Device Handler -----------------------
    private boolean isDeviceInUse(User user, DeviceInfo device) {
        return deviceInfoRepository.findByUserAndDeviceInUse(user.getId())
                .map(d -> d.getDeviceId().equals(device.getDeviceId()))
                .orElse(false);
    }

    public boolean verifyOTP(User user, String otp) {
        UserOtp userOtp = userOtpRepository.findByUserId(user.getId());
        if (userOtp == null) {
            System.out.println("No OTP found for user: " + user.getId());
            return false;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("Verifying OTP for user: " + user.getId() + ", currentTime: " + currentTime + ", otpEndTime: " + userOtp.getTimeEnd());
        if (userOtp.getTimeEnd().isBefore(currentTime)) {
            System.out.println("OTP expired for user: " + user.getId());
            return false;
        }
        boolean isValid = otp.equals(userOtp.getOtpCode());

        return isValid;
    }

    public Optional<DeviceInfo> findDeviceByIdAndUser(String deviceId, User user) {
        return deviceInfoRepository.findByDeviceIdAndUser(deviceId, user);
    }

    public Optional<DeviceInfo> findByInforOfDevice(String currentDeviceName, String currentBrowser, String currentOs,String userId) {
        return deviceInfoRepository.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs,userId);
    }

    public void saveDevice(DeviceInfo device) {
        deviceInfoRepository.save(device);
    }

    private DeviceInfo createDeviceInfo(HttpSession session, Browser browser, OperatingSystem os, User user,
                                        HttpServletRequest request) {
        if (user == null || user.getId() == null) {
            System.err.println("Error: User or userId is null in createDeviceInfo");
            throw new IllegalArgumentException("User cannot be null");
        }
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(session.getId());
        deviceInfo.setUser(user);
        deviceInfo.setIpAddress(request.getRemoteAddr());

        String browserName = browser != null && browser != Browser.UNKNOWN ? browser.getName() : "Unknown Browser";
        deviceInfo.setBrowser(browserName);

        String osName = os != null && os != OperatingSystem.UNKNOWN ? os.getName() : "Unknown OS";
        deviceInfo.setOs(osName);

        String deviceName = "Unknown Device";
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && !userAgent.isBlank()) {
            if (userAgent.toLowerCase().contains("mobile")) {
                deviceName = "Mobile Device";
            } else if (userAgent.contains("Windows")) {
                deviceName = "Windows PC";
            } else if (userAgent.contains("iPhone")) {
                deviceName = "iPhone Device";
            } else if (userAgent.contains("Macintosh")) {
                deviceName = "Mac";
            }
        }
        deviceInfo.setDeviceName(deviceName);
        deviceInfo.setCreatedAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        deviceInfo.setLastLoginAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        deviceInfo.setInUse(false);
        return deviceInfo;
    }

    @Transactional
    public String createOtp(User user) {
        if (user == null || user.getId() == null) {
            System.err.println("Error: User or userId is null in createOtp");
            throw new IllegalArgumentException("User cannot be null");
        }
        System.out.println("Creating OTP for userId: " + user.getId());
        int otpCode = 100000 + new Random().nextInt(900000);
        try {
            System.out.println("Attempting to delete OTP for user: " + user.getId());

            UserOtp checkAfterDelete = userOtpRepository.findByUserId(user.getId());
            System.out.println("After delete, OTP record: " + (checkAfterDelete == null ? "none" : checkAfterDelete.getTimeEnd()));
            
            UserOtp userOtp;
            if(userOtpRepository.findByUser(user).isEmpty())
            {
            	 userOtp = new UserOtp();
                 userOtp.randomId();
                 userOtp.setUser(user);
                 userOtp.setOtpCode(String.valueOf(otpCode));
                 LocalDateTime startTime = LocalDateTime.now();
                 LocalDateTime endTime = startTime.plusMinutes(5);
                 userOtp.setTimeStart(startTime);
                 userOtp.setTimeEnd(endTime);
                 System.out.println("Creating OTP for user: " + user.getId() + ", startTime: " + startTime + ", endTime: " + endTime);

                 UserOtp savedOtp = userOtpRepository.saveAndFlush(userOtp);
                 System.out.println("Saved OTP for user: " + user.getId() + ", savedTimeEnd: " + (savedOtp != null ? savedOtp.getTimeEnd() : "null"));
                 if (savedOtp == null) {
                     throw new RuntimeException("Failed to save OTP for user: " + user.getId());
                 }
            }
            else
            {
            	userOtp=userOtpRepository.findByUser(user).get();
                userOtp.setUser(user);
                userOtp.setOtpCode(String.valueOf(otpCode));
                LocalDateTime startTime = LocalDateTime.now();
                LocalDateTime endTime = startTime.plusMinutes(5);
                userOtp.setTimeStart(startTime);
                userOtp.setTimeEnd(endTime);
                System.out.println("Update OTP for user: " + user.getId() + ", startTime: " + startTime + ", endTime: " + endTime);

                UserOtp savedOtp = userOtpRepository.saveAndFlush(userOtp);
                System.out.println("Saved OTP for user: " + user.getId() + ", savedTimeEnd: " + (savedOtp != null ? savedOtp.getTimeEnd() : "null"));
                if (savedOtp == null) {
                    throw new RuntimeException("Failed to save OTP for user: " + user.getId());
                }
            }
            
           
            return String.valueOf(otpCode);
        } catch (Exception e) {
            System.err.println("Error in createOtp for user: " + user.getId() + ", message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create OTP", e);
        }
    }

    private void sendDeviceNotification(User user, DeviceInfo device) {
        if (user == null || user.getId() == null || user.getEmail() == null) {
            System.err.println("Error: Invalid user data in sendDeviceNotification");
            throw new IllegalArgumentException("User or user email cannot be null");
        }
        try {
            String otp = createOtp(user);
            emailDeviceService.sendOtpEmail(user, device, otp);
            System.out.println("Sent device notification for user: " + user.getId() + ", email: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Error sending device notification for user: " + user.getId() + ", message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send device notification", e);
        }
    }

    private void sendFirstLoginNotification(User user, DeviceInfo device) {
        if (user == null || user.getId() == null || user.getEmail() == null) {
            System.err.println("Error: Invalid user data in sendFirstLoginNotification");
            throw new IllegalArgumentException("User or user email cannot be null");
        }
        try {
            String otp = createOtp(user);
            emailDeviceService.sendOtpEmail(user, device, otp);
            System.out.println("Sent first login notification for user: " + user.getId() + ", email: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Error sending first login notification for user: " + user.getId() + ", message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send first login notification", e);
        }
    }

    private String buildDeviceMessage(User user, DeviceInfo device, String otp, boolean isFirstLogin) {
        String title = isFirstLogin ? "Chúng tôi nhận thấy bạn là người dùng mới"
                : "Tài khoản của bạn đang đăng nhập từ thiết bị khác";
        String deviceName = device.getDeviceName() != null ? device.getDeviceName() : "Unknown Device";

        return String.format("""
                Xin chào %s,

                %s:

                🔹 Thiết bị: %s
                🔹 Hệ điều hành: %s
                🔹 Trình duyệt: %s
                🔹 IP: %s
                🔹 Thời gian: %s

                🔹 Mã OTP (hiệu lực 5 phút): %s

                Nếu không phải bạn, vui lòng đổi mật khẩu ngay!
                """, user.getFullName() != null ? user.getFullName() : "User", title, deviceName, device.getOs(),
                device.getBrowser(), device.getIpAddress(),
                device.getLastLoginAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), otp);
    }

    // ------------------ Basic Auth + Logout -----------------------
    public void logout(HttpSession session) {
        session.invalidate();
    }

    public AuthResponse authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return new AuthResponse(username, jwtUtil.generateAccessToken(username),
                jwtUtil.generateRefreshToken(username));
    }

    public List<Optional<DeviceInfo>> getAllDeviceFromUser(String userId) {
        return deviceInfoRepository.getAllDeviceWasLoginByUser(userId);
    }
    
    public List<DeviceInfo> getAllDeviceFromUser2(String userId) {
        return deviceInfoRepository.getAllDeviceWasLoginByUser2(userId);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    private UserAuthResponse buildUserAuthResponse(User user, String role, boolean rememberMe) {
        UserAuthResponse response = new UserAuthResponse();
        response.setId(user.getId());
        response.setRole(role);
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setUsername(user.getUsername());
        response.setAvatar(user.getAvatar());
        response.setKycStatus(user.getKycStatus());
        response.setWalletAddress(user.getWalletAddress());
        response.setFirstName(user.getFirstName());
        response.setRememberMe(rememberMe);
        return response;
    }

    @Transactional
    public AuthenticationResponse verifyOtp(String otp, String email, DeviceInfo deviceInfo) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        UserOtp userOtp = userOtpRepository.findByUserId(user.getId());
        System.out.println("Found OTP for user: " + user.getId() + ", otpId: " + (userOtp != null ? userOtp.getId() : "null") + ", otpCode: " + (userOtp != null ? userOtp.getOtpCode() : "null") + ", timeEnd: " + (userOtp != null ? userOtp.getTimeEnd() : "null"));
        if (userOtp == null) {
            throw new IllegalArgumentException("No OTP found for user");
        }
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("Verifying OTP for user: " + user.getId() + ", currentTime: " + currentTime + ", otpEndTime: " + userOtp.getTimeEnd());
        if (userOtp.getTimeEnd().isBefore(currentTime)) {
            throw new IllegalArgumentException("Mã OTP đã hết hạn");
        }
        if (!userOtp.getOtpCode().equals(otp)) {
            throw new IllegalArgumentException("Mã OTP không đúng");
        }

        DeviceInfo savedDeviceInfo = emailDeviceService.updateDeviceInfo(deviceInfo, user.getId());
        String role = userService.getUserRole(user.getId())
                .map(userRole -> userRole.getRole().getName())
                .orElse("USER");
        UserAuthResponse userResponse = buildUserAuthResponse(user, role, true);
        return AuthenticationResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user.getId()))
                .refreshToken(jwtUtil.generateRefreshToken(user.getId()))
                .deviceInfo(savedDeviceInfo)
                .user(userResponse)
                .build();
    }

    public void createDebitAccount(User user) {
        DebitWallet debitWallet = new DebitWallet();
        debitWallet.setUser(user);
        debitWallet.setBalance(BigDecimal.ZERO);
        debitWallet.setCreatedAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        debitWallet.setDeleted(false);
        debitWalletRepository.save(debitWallet);
    }

}