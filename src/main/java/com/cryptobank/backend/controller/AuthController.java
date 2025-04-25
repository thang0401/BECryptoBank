package com.cryptobank.backend.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.cryptobank.backend.DTO.*;
import com.cryptobank.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDAO userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private DeviceInforDAO deviceInfoRepository;
    
    @Autowired
    private UserOtpRepository userOtpRepository;

    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil


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
            if (e.getMessage().contains("OTP verification required")) {
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
            if ("Google login failed: OTP verification required".contains(e.getMessage())) {
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("message", "Đưa đến trang nhập mã OTP xác thực");
                // Lưu userId vào session
                session.setAttribute("otpUserId", e.getUserId());
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
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

            if (userId == null || otp == null) {
                return ResponseEntity.badRequest().body("Thiếu tham số: user_id hoặc otp");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException("Không tìm thấy người dùng với ID: " + userId));

            // Kiểm tra OTP và thời gian hết hạn
            UserOtp userOtp = userOtpRepository.findByUserId(userId);
            if (userOtp == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không tồn tại");
            }
            if (userOtp.getTimeEnd().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP đã hết hạn");
            }
            if (!authService.verifyOTP(user, otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không hợp lệ");
            }

            // Xóa OTP sau khi xác minh thành công
            userOtpRepository.delete(userOtp);

            String userAgent = servletRequest.getHeader("User-Agent");
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            Browser browser = ua.getBrowser();
            OperatingSystem os = ua.getOperatingSystem();

            String currentBrowser = browser.getName();
            String currentOs = os.getName();
            String currentDeviceName = "Unknown Device";
            if (userAgent != null && !userAgent.isBlank()) {
                if (userAgent.toLowerCase().contains("mobile")) {
                    currentDeviceName = "Mobile Device";
                } else if (userAgent.contains("Windows")) {
                    currentDeviceName = "Windows PC";
                } else if (userAgent.contains("iPhone")) {
                    currentDeviceName = "iPhone Device";
                } else if (userAgent.contains("Macintosh")) {
                    currentDeviceName = "Mac";
                }
            }

            // Cập nhật DeviceInfo
            Optional<DeviceInfo> deviceOpt = authService.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs,userId);
            if (!deviceOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy thiết bị");
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

            // Tạo token với thông tin người dùng
            String role = userService.getUserRole(user.getId())
                    .map(userRole -> userRole.getRole().getName())
                    .orElse("USER");
            UserInformation userInformation = userService.convertToUserInformation(user); // Chuyển User thành UserInformation
            String accessToken = jwtUtil.generateToken(userInformation, 1000 * 60 * 30); // 30 phút

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("userData", buildUserAuthResponse(user, role, rememberMe));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xác minh OTP thất bại: " + e.getMessage());
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
            Optional<DeviceInfo> deviceOpt = authService.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs,user.getId());
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