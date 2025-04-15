package com.cryptobank.backend.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cryptobank.backend.DTO.*;
import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserOtp;
import com.cryptobank.backend.repository.DeviceInforDAO;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.UserOtpRepository;
import com.cryptobank.backend.services.AuthService;
import com.cryptobank.backend.services.UserService;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDAO userRepository;

    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DeviceInforDAO deviceInfoRepository;
    
    @Autowired
    private UserOtpRepository userOtpRepository;

    public AuthController() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @PostMapping("/login/email")
    public ResponseEntity<?> loginWithEmail(
            @RequestBody LoginRequestAuth login,
            HttpServletRequest request,
            HttpSession session) {
        try {
            UserAuthResponse response = authService.loginWithEmail(
                    login.getEmail(),
                    login.getPassword(),
                    login.isRememberMe(),
                    request,
                    session
            );
            return ResponseEntity.ok(response);
        } catch (com.cryptobank.backend.exception.AuthException e) {
            if ("Login failed: OTP verification required".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Đưa đến trang nhập mã OTP xác thực");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Đăng nhập thất bại: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(
            @RequestBody GoogleLoginRequest request,
            HttpServletRequest servletRequest,
            HttpSession session) {
        if (request.getIdToken() == null || request.getIdToken().isBlank()) {
            return ResponseEntity.badRequest().body("Missing required parameter: idToken");
        }
        try {
            UserAuthResponse response = authService.loginWithGoogle(
                    request.getIdToken(),
                    request.isRememberMe(),
                    servletRequest,
                    session
            );
            return ResponseEntity.ok(response);
        } catch (com.cryptobank.backend.exception.AuthException e) {
            if ("OTP verification required".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Đưa đến trang nhập mã OTP xác thực");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google login failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Google login failed: " + e.getMessage());
        }
    }

    @PostMapping("/login/google/OTP")
    public ResponseEntity<?> verifyGoogleOTP(
            @RequestBody OtpRequest request,
            HttpServletRequest servletRequest,
            HttpSession session) {
        try {
            String userId = request.getUser_id();
            String otp = request.getOtp();
            boolean rememberMe = request.isRememberMe();

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));

            // Xác thực OTP
            if (!authService.verifyOTP(user, otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
            }

            // Cập nhật DeviceInfo
            Optional<DeviceInfo> deviceOpt = authService.findDeviceByIdAndUser(session.getId(), user);
            if (!deviceOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Device not found");
            }

            DeviceInfo device = deviceOpt.get();
            String userAgent = servletRequest.getHeader("User-Agent");
            if (userAgent != null && !userAgent.isBlank()) {
                UserAgent ua = UserAgent.parseUserAgentString(userAgent);
                Browser browser = ua.getBrowser();
                OperatingSystem os = ua.getOperatingSystem();

                // Cập nhật browser
                String browserName = "Unknown Browser";
                if (browser != null && browser != Browser.UNKNOWN) {
                    browserName = browser.getName();
                    if (browserName.startsWith("CHROME")) {
                        browserName = "Chrome";
                    }
                }
                device.setBrowser(browserName);

                // Cập nhật OS
                String osName = "Unknown OS";
                if (os != null && os != OperatingSystem.UNKNOWN) {
                    osName = os.getName();
                    if (osName.equals("WINDOWS_10")) {
                        osName = "Windows 10";
                    }
                }
                device.setOs(osName);
            }

            // Đặt in_use = true cho thiết bị hiện tại
            device.setLastLoginAt(OffsetDateTime.now());
            device.setInUse(true);
            authService.saveDevice(device);

            // Đặt in_use = false cho tất cả thiết bị khác của user
            List<DeviceInfo> otherDevices = deviceInfoRepository.findAllByUserAndDeviceIdNot(user, session.getId());
            for (DeviceInfo otherDevice : otherDevices) {
                otherDevice.setInUse(false);
                authService.saveDevice(otherDevice);
            }

            // Trả về thông tin user
            String role = userService.getUserRole(user.getId())
                .map(userRole -> userRole.getRole().getName())
                .orElse("USER");
            return ResponseEntity.ok(buildUserAuthResponse(user, role, rememberMe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OTP verification failed: " + e.getMessage());
        }
    }

    @PostMapping("/login/email/OTP")
    public ResponseEntity<?> verifyEmailOTP(
            @RequestBody OtpRequest request,
            HttpServletRequest servletRequest,
            HttpSession session) {
        try {
            String userId = request.getUser_id();
            String otp = request.getOtp();
            boolean rememberMe = request.isRememberMe();

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));

            //Kiểm tra thời gian
            UserOtp userOtp = userOtpRepository.findByUserId(user.getId());
            if(userOtp.getTimeEnd().isBefore(LocalDateTime.now()))
            {
            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP has out of time");
            }
            
            
            // Xác thực OTP
            if (!authService.verifyOTP(user, otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
            }
            
            String userAgent = servletRequest.getHeader("User-Agent");
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            Browser browser = ua.getBrowser();
            OperatingSystem os = ua.getOperatingSystem();

            String currentBrowser = browser.getName();
            String currentOs = os.getName();
            String currentDeviceName = "Unknown Device";
            if (userAgent != null && !userAgent.isBlank()) {
                // Suy ra loại thiết bị từ User-Agent
                if (userAgent.toLowerCase().contains("mobile")) {
                	currentDeviceName = "Mobile Device";
                } else if (userAgent.contains("Windows")) {
                	currentDeviceName = "Windows PC";
                } 
                else if (userAgent.contains("iPhone")) {
                	currentDeviceName = "iPhone Device";
                }else if (userAgent.contains("Macintosh")) {
                	currentDeviceName = "Mac";
                }
                
            }
           
            // Cập nhật DeviceInfo
            Optional<DeviceInfo> deviceOpt = authService.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs);
            if (!deviceOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Device not found");
            }

            DeviceInfo device = deviceOpt.get();
            device.setBrowser(currentBrowser);
            device.setOs(currentOs);


            // Đặt in_use = false cho tất cả thiết bị khác của user
            List<DeviceInfo> otherDevices = deviceInfoRepository.findAllByUser(user);
            for (DeviceInfo otherDevice : otherDevices) {
                otherDevice.setInUse(false);
                authService.saveDevice(otherDevice);
            }
            
            // Đặt in_use = true cho thiết bị hiện tại
            device.setLastLoginAt(OffsetDateTime.now());
            device.setInUse(true);
            authService.saveDevice(device);

            // Trả về thông tin user
            String role = userService.getUserRole(user.getId())
                .map(userRole -> userRole.getRole().getName())
                .orElse("USER");
            return ResponseEntity.ok(buildUserAuthResponse(user, role, rememberMe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OTP verification failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Đăng xuất thành công");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestResetPassword(@RequestBody String email, HttpSession session) {
        try {
            userService.requestResetPassword(email, session);
            return ResponseEntity.ok("Mã xác thực đã được gửi đến email của bạn");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request,
            HttpSession session) {
        try {
            userService.resetPassword(
                    request.getEmail(),
                    request.getResetCode(),
                    request.getNewPassword(),
                    session
            );
            return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getAllDevice/{userId}")
    public ResponseEntity<List<Optional<DeviceInfo>>> getAllDeviceFromUser(@PathVariable String userId) {
        List<Optional<DeviceInfo>> listDevice = authService.getAllDeviceFromUser(userId);
        return ResponseEntity.ok(listDevice);
    }

    @PostMapping("/accesstoken")
    public AuthResponse getAccessToken(
            @RequestParam String username,
            @RequestParam String password) {
        return authService.authenticate(username, password);
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

    private static NameSplit getFirstAndLastName(String fullname) {
        String[] nameParts = fullname.split("\\s+");
        if (nameParts.length == 1) {
            return new NameSplit(nameParts[0], "");
        }
        String firstName = nameParts[nameParts.length - 1];
        String lastName = String.join(" ", java.util.Arrays.copyOfRange(nameParts, 0, nameParts.length - 1));
        return new NameSplit(firstName, lastName);
    }
}