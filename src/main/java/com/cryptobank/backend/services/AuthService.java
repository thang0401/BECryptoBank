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

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            User user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new AuthException("User not found"));

            if (loginResult == 0) {
                String role = userService.getUserRole(user.getId())
                    .map(userRole -> userRole.getRole().getName())
                    .orElse("USER");
                return buildUserAuthResponse(user, role, rememberMe);
            } else if (loginResult == 1) {
                throw new AuthException("OTP verification required");
            } else {
                throw new AuthException("Invalid Email or Password");
            }
        } catch (Exception e) {
            throw new AuthException( e.getMessage());
        }
    }

    private int handleLogin(String email, String password, HttpServletRequest request, HttpSession session) {
        User user =Optional.ofNullable(userRepository.findByEmail(email)).orElse(null);
        System.out.println(password);
        System.out.println(passwordEncoder.encode(password));
        if (user == null) {
            throw new AuthException("Invalid Email");
        }
        else if(!passwordEncoder.matches(password, user.getPassword()))
        {
        	throw new AuthException("Invalid Password");
        }
//        System.out.println("Before "+user.getPassword());
//        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
//        userRepository.save(user);
//        System.out.println("After "+user.getPassword());

        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        Browser browser = ua.getBrowser();
        OperatingSystem os = ua.getOperatingSystem();

        // Tạo thông tin thiết bị hiện tại (chưa lưu vào DB)
        DeviceInfo currentDevice = createDeviceInfo(session, browser, os, user, request);
        String currentBrowser = currentDevice.getBrowser();
        String currentOs = currentDevice.getOs();
        String currentDeviceName = currentDevice.getDeviceName();

        // Lấy tất cả thiết bị của user có in_use = true
        Optional<DeviceInfo> activeDeviceOpt = deviceInfoRepository.findByUserAndDeviceInUse(user.getId());
        if (activeDeviceOpt.isPresent()) {
            DeviceInfo activeDevice = activeDeviceOpt.get();
            // So sánh thiết bị hiện tại với thiết bị active
            boolean isSameDevice = activeDevice.getBrowser().equals(currentBrowser) &&
                                   activeDevice.getOs().equals(currentOs) &&
                                   activeDevice.getDeviceName().equals(currentDeviceName);
            if (isSameDevice) {
                // Nếu là cùng thiết bị, cập nhật lastLoginAt và không cần OTP
                activeDevice.setLastLoginAt(OffsetDateTime.now());
                deviceInfoRepository.save(activeDevice);
                return 0;
            } else {
                // Nếu là thiết bị khác, tìm thiết bị theo device_id
                Optional<DeviceInfo> deviceOpt = deviceInfoRepository.findByInforOfDevice(currentDeviceName,currentBrowser,currentOs);
                if (deviceOpt.isPresent()) {
                    // Thiết bị đã tồn tại nhưng chưa xác thực
                    sendDeviceNotification(user, deviceOpt.get());
                    return 1;
                } else {
                    // Thiết bị mới, lưu và gửi thông báo
                    DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
                    deviceInfoRepository.save(newDevice);
                    sendFirstLoginNotification(user, newDevice);
                    return 1;
                }
            }
        } else {
            // Không có thiết bị nào active, coi đây là thiết bị mới
            DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
            deviceInfoRepository.save(newDevice);
            sendFirstLoginNotification(user, newDevice);
            return 1;
        }
    }

    // ---------------- LOGIN with Google ------------------
    @Transactional
    public UserAuthResponse loginWithGoogle(String idToken, boolean rememberMe, HttpServletRequest request,
                                            HttpSession session) {
        try {
            GoogleIdToken token = googleTokenVerifier.verify(idToken);
            if (token == null) {
                throw new AuthException("Invalid Google ID Token");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String googleId = token.getPayload().getSubject();

            User user = getOrCreateGoogleUser(payload, googleId);
            user.setLastLoginAt(OffsetDateTime.now());
            user = userRepository.save(user);
            System.out.println("User saved, userId: " + user.getId());
            if (user.getId() == null) {
                System.out.println("Error: userId is null after saving user");
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

            Optional<DeviceInfo> deviceOpt = deviceInfoRepository.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs);
            if (deviceOpt.isPresent()) {
                if (!isDeviceInUse(user, deviceOpt.get())) {
                    System.out.println("User ID before throwing exception (existing device): " + user.getId());
                    if (user.getId() == null) {
                        System.out.println("Error: userId is null before throwing OTP exception (existing device)");
                        throw new IllegalStateException("User ID is null before throwing OTP exception");
                    }
                    sendDeviceNotification(user, deviceOpt.get());
                    AuthException authException = new AuthException("OTP verification required", user.getId());
                    System.out.println("AuthException created with userId: " + authException.getUserId());
                    throw authException;
                } else {
                    DeviceInfo device = deviceOpt.get();
                    device.setLastLoginAt(OffsetDateTime.now());
                    deviceInfoRepository.save(device);
                }
            } else {
                DeviceInfo newDevice = createDeviceInfo(session, browser, os, user, request);
                deviceInfoRepository.save(newDevice);
                System.out.println("User ID before throwing exception (new device): " + user.getId());
                if (user.getId() == null) {
                    System.out.println("Error: userId is null before throwing OTP exception (new device)");
                    throw new IllegalStateException("User ID is null before throwing OTP exception");
                }
                sendFirstLoginNotification(user, newDevice);
                throw new AuthException("OTP verification required", user.getId());
            }

            String role = userService.getUserRole(user.getId())
                    .map(userRole -> userRole.getRole().getName())
                    .orElse("USER");

            return buildUserAuthResponse(user, role, rememberMe);
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
            user.setPassword(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(10)));
            user.setCreatedAt(OffsetDateTime.now());
        }

        user = userRepository.save(user);
        entityManager.refresh(user); // Làm mới entity
        System.out.println("User saved in getOrCreateGoogleUser, userId: " + user.getId());

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

    public boolean verifyOTP(User user, String otp) {
        UserOtp userOtp = userOtpRepository.findByUserId(user.getId());
        if (userOtp == null) {
            return false;
        }
        return otp.equals(userOtp.getOtpCode());
    }

    public Optional<DeviceInfo> findDeviceByIdAndUser(String deviceId, User user) {
        return deviceInfoRepository.findByDeviceIdAndUser(deviceId, user);
    }
    
    public Optional<DeviceInfo> findByInforOfDevice(String currentDeviceName,String currentBrowser,String currentOs)
    {
    	return deviceInfoRepository.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs);
    }

    public void saveDevice(DeviceInfo device) {
        deviceInfoRepository.save(device);
    }

    private DeviceInfo createDeviceInfo(HttpSession session, Browser browser, OperatingSystem os, User user,
            HttpServletRequest request) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(session.getId());
        deviceInfo.setUser(user);
        deviceInfo.setIpAddress(request.getRemoteAddr());

        // Gán browser
        String browserName = "Unknown Browser";
        if (browser != null && browser != Browser.UNKNOWN) {
            browserName = browser.getName();  
        }
        deviceInfo.setBrowser(browserName);

        // Gán OS
        String osName = "Unknown OS";
        if (os != null && os != OperatingSystem.UNKNOWN) {
            osName = os.getName();
        }
        deviceInfo.setOs(osName);
        
     // Gán deviceName
        String deviceName = "Unknown Device";
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && !userAgent.isBlank()) {
            // Suy ra loại thiết bị từ User-Agent
            if (userAgent.toLowerCase().contains("mobile")) {
                deviceName = "Mobile Device";
            } else if (userAgent.contains("Windows")) {
                deviceName = "Windows PC";
            } 
            else if (userAgent.contains("iPhone")) {
                deviceName = "iPhone Device";
            }else if (userAgent.contains("Macintosh")) {
                deviceName = "Mac";
            }
            
        }

        deviceInfo.setDeviceName(deviceName); // Có thể thêm logic để lấy tên thiết bị
        deviceInfo.setCreatedAt(OffsetDateTime.now());
        deviceInfo.setLastLoginAt(OffsetDateTime.now());
        deviceInfo.setInUse(false);
        return deviceInfo;
    }

    private String createOtp(User user) {
        int otpCode = 100000 + new Random().nextInt(900000);
        UserOtp userOtp = userOtpRepository.findByUserId(user.getId());
        if (userOtp == null) {
            userOtp = new UserOtp();
            userOtp.randomId();
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
        String subject = "Đăng nhập từ thiết bị khác!";
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
        String title = isFirstLogin ? "Chúng tôi nhận thấy bạn là người dùng mới"
                : "Tài khoản của bạn đang đăng nhập từ thiết bị khác";
        
     // Xử lý trường hợp deviceName là null
        String deviceName = device.getDeviceName() != null ? device.getDeviceName() : "Unknown Device";

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
                """, user.getFullName(), title, deviceName, device.getOs(),
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
        response.setPassword(user.getPassword());
        response.setAvatar(user.getAvatar());
        response.setKycStatus(user.getKycStatus());
        response.setWalletAddress(user.getWalletAddress());
        response.setFirstName(user.getFirstName());
        response.setRememberMe(rememberMe);
        return response;
    }
}