package com.cryptobank.backend.controller;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.cryptobank.backend.DTO.*;
import com.cryptobank.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserOtp;
import com.cryptobank.backend.repository.DebitWalletDAO;
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
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowCredentials = "true")
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
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserOtpRepository otpRepository;
    
    @Autowired
    private DebitWalletDAO debitWalletRepository;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    @PostMapping("/SignUp")
    public ResponseEntity<?> registerNewUser(@RequestBody UserRegisterDTO userInfor)
    {
    	try {
    		User userCheck=userRepository.findByEmail(userInfor.getGmail());
    		if(userCheck==null)
        	{
        		User userNew=new User();
        		userNew.setEmail(userInfor.getGmail());
        		userNew.setPassword(passwordEncoder.encode(userInfor.getPassword()));
        		userNew.setCreatedAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        		userNew.setKycStatus(false);
        		userCheck=null;
        		userCheck=userRepository.save(userNew);
        		if(userCheck==null)
        		{
        			return ResponseEntity.badRequest().body("Lỗi khi lưu thông tin user mới ");
        		}
        		else
        		{
        			System.out.println("Tạo user Thành Công"+userCheck);
        			DebitWallet debitWalletNew=new DebitWallet();
        			debitWalletNew.setBalance(BigDecimal.valueOf(0));
        			debitWalletNew.setUser(userCheck);
        			debitWalletNew.setCreatedAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        			debitWalletNew.setDeleted(false);
        			debitWalletRepository.save(debitWalletNew);
        			System.out.println("Tạo Debit Wallet Thành Công: "+debitWalletNew);
        			return ResponseEntity.ok("Đăng ký tài khoản thành công");
        		}
        	}
    		else
    		{
    			return ResponseEntity.badRequest().body("Email đã tồn tại vui lòng thử lại");
    		}
		} catch (Exception e) {
			System.out.println("Chi tiết lỗi: "+e.toString());
			ResponseEntity.badRequest().body("Lỗi trong quá trình tạo tài khoản mới");
		}
		return ResponseEntity.ok("Lỗi khi đăng ký tài khoản người dùng mới");
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
        	 Map<String, Object> response = authService.loginWithGoogle(
                    request.getIdToken(),
                    request.isRememberMe(),
                    servletRequest,
                    session
            );
            return ResponseEntity.ok(response);
        } catch (com.cryptobank.backend.exception.AuthException e) {
            if ("Google login failed: OTP verification required".contains(e.getMessage())) {
            	String userId = (String) session.getAttribute("otpUserId");
                System.out.println("OTP required, user_id: " + e.getUserId());
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("message", "Đưa đến trang nhập mã OTP xác thực");
                if (e.getUserId() != null) {
                    session.setAttribute("otpUserId",userId);
                }
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
            String gmail = request.getGmail();
            String otp = request.getOtp();
            boolean rememberMe = request.isRememberMe();

            if (gmail == null || otp == null) {
                return ResponseEntity.badRequest().body("Thiếu tham số: gmail hoặc otp");
            }

            User user = userRepository.findByEmail(gmail);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy người dùng với email: " + gmail);
            }

            UserOtp userOtp = userOtpRepository.findByUserId(user.getId());
            if (userOtp == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không tồn tại");
            }
            System.out.println("Verifying OTP for user: " + user.getId() + ", currentTime: " + LocalDateTime.now() + ", otpEndTime: " + userOtp.getTimeEnd());
            if (userOtp.getTimeEnd().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP đã hết hạn");
            }
            if (!authService.verifyOTP(user, otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không hợp lệ");
            }

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
            
            Optional<DeviceInfo> deviceOpt = authService.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs,user.getId());
            if (!deviceOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy thiết bị");
            }

            DeviceInfo device = deviceOpt.get();
            device.setBrowser(currentBrowser);
            device.setOs(currentOs);

            List<DeviceInfo> otherDevices = deviceInfoRepository.findAllByUser(user);
            for (DeviceInfo otherDevice : otherDevices) {
                otherDevice.setInUse(false);
                authService.saveDevice(otherDevice);
            }

            device.setLastLoginAt(OffsetDateTime.now());
            device.setInUse(true);
            authService.saveDevice(device);

            String role = userService.getUserRole(user.getId())
                    .map(userRole -> userRole.getRole().getName())
                    .orElse("USER");
            UserInformation userInformation = userService.convertToUserInformation(user);
            String accessToken = jwtUtil.generateToken(userInformation, 1000 * 60 * 30);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("userData", buildUserAuthResponse(user, role, rememberMe));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xác minh OTP thất bại: " + e.getMessage());
        }
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
    
    @PostMapping("/login/email/OTP")
    public ResponseEntity<?> verifyEmailOTP(
            @RequestBody OtpRequest request,
            HttpServletRequest servletRequest,
            HttpSession session) {
        try {
            String gmail = request.getGmail();
            String otp = request.getOtp();
            boolean rememberMe = request.isRememberMe();

            User user = userRepository.findByEmail(gmail);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy người dùng với email: " + gmail);
            }

            UserOtp userOtp = userOtpRepository.findByUserId(user.getId());
            if (userOtp == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không tồn tại");
            }
            System.out.println("Verifying OTP for user: " + user.getId() + ", currentTime: " + LocalDateTime.now() + ", otpEndTime: " + userOtp.getTimeEnd());
            if (userOtp.getTimeEnd().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP đã hết hạn");
            }
            if (!authService.verifyOTP(user, otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không hợp lệ");
            }

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

            Optional<DeviceInfo> deviceOpt = authService.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs,user.getId());
            if (!deviceOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy thiết bị");
            }

            DeviceInfo device = deviceOpt.get();
            device.setBrowser(currentBrowser);
            device.setOs(currentOs);

            List<DeviceInfo> otherDevices = deviceInfoRepository.findAllByUser(user);
            for (DeviceInfo otherDevice : otherDevices) {
                otherDevice.setInUse(false);
                authService.saveDevice(otherDevice);
            }

            device.setLastLoginAt(OffsetDateTime.now());
            device.setInUse(true);
            authService.saveDevice(device);

            String role = userService.getUserRole(user.getId())
                    .map(userRole -> userRole.getRole().getName())
                    .orElse("USER");
            return ResponseEntity.ok(buildUserAuthResponse(user, role, rememberMe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xác minh OTP thất bại: " + e.getMessage());
        }
    }

//    @PostMapping("/resend-otp")
//    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request, HttpServletRequest servletRequest, HttpSession session) {
//        try {
//            String gmail = request.get("email");
//            if (gmail == null) {
//                return ResponseEntity.badRequest().body("Thiếu tham số: email");
//            }
//
//            User user = userRepository.findByEmail(gmail);
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy người dùng với email: " + gmail);
//            }
//
//            // Kiểm tra thời gian gửi OTP cuối cùng
//            LocalDateTime lastOtpSent = (LocalDateTime) session.getAttribute("lastOtpSent_" + user.getId());
//            if (lastOtpSent != null && lastOtpSent.plusSeconds(30).isAfter(LocalDateTime.now())) {
//                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Vui lòng đợi 30 giây trước khi yêu cầu OTP mới");
//            }
//
//            String userAgent = servletRequest.getHeader("User-Agent");
//            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
//            Browser browser = ua.getBrowser();
//            OperatingSystem os = ua.getOperatingSystem();
//
//            String currentBrowser = browser.getName();
//            String currentOs = os.getName();
//            String currentDeviceName = "Unknown Device";
//            if (userAgent != null && !userAgent.isBlank()) {
//                if (userAgent.toLowerCase().contains("mobile")) {
//                    currentDeviceName = "Mobile Device";
//                } else if (userAgent.contains("Windows")) {
//                    currentDeviceName = "Windows PC";
//                } else if (userAgent.contains("iPhone")) {
//                    currentDeviceName = "iPhone Device";
//                } else if (userAgent.contains("Macintosh")) {
//                    currentDeviceName = "Mac";
//                }
//            }
//
//            Optional<DeviceInfo> deviceOpt = authService.findByInforOfDevice(currentDeviceName, currentBrowser, currentOs);
//            if (!deviceOpt.isPresent()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy thiết bị");
//            }
//
//            authService.sendDeviceNotification(user, deviceOpt.get());
//            session.setAttribute("lastOtpSent_" + user.getId(), LocalDateTime.now());
//            return ResponseEntity.ok("Mã OTP mới đã được gửi đến email của bạn");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gửi lại OTP thất bại: " + e.getMessage());
//        }
//    }

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
    public ResponseEntity<List<DeviceInforDTO>> getAllDeviceFromUser(@PathVariable String userId) {
        List<DeviceInforDTO> listDevice = authService.getAllDeviceFromUser2(userId).stream()
        												   .map(dev -> new DeviceInforDTO(
        														dev.getDeviceId(),
        														dev.getDeviceName(),
        														dev.getBrowser(),
        														dev.getIpAddress(),
        														dev.getOs(),
        														dev.getLastLoginAt(),
        														dev.getInUse(),
        														dev.getUser().getId(),
        														dev.getUser().getFullName()
        													))
        												   .collect(Collectors.toList());
        return ResponseEntity.ok(listDevice);
    }
    
    @GetMapping("/getUserOtp/all")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllUserOtp() {
        List<UserOtpDTO> listUserOtp = otpRepository.findAllWithUser().stream()
            .map(userOtp -> new UserOtpDTO(
                userOtp.getId(),
                userOtp.getUser().getId(),
                userOtp.getUser().getFullName(),
                userOtp.getTimeStart(),
                userOtp.getOtpCode(),
                userOtp.getTimeEnd()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(listUserOtp);
    }
    
    @GetMapping("/getUserOtp/{userId}")
    public ResponseEntity<?> getUserOtpById(@PathVariable String userId)
    {
    	 UserOtp userOtp = otpRepository.findByUserId(userId);
    	 UserOtpDTO userDTO=new UserOtpDTO(
    		        userOtp.getId(),
    		        userOtp.getUser().getId(),
    		        userOtp.getUser().getFullName(),
    		        userOtp.getTimeStart(),
    		        userOtp.getOtpCode(),
    		        userOtp.getTimeEnd()
    		    );
    	 
    	return ResponseEntity.ok(userDTO);
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
        response.setAvatar(user.getAvatar());
        response.setKycStatus(user.getKycStatus());
        response.setWalletAddress(user.getWalletAddress());
        response.setFirstName(user.getFirstName());
        response.setRememberMe(rememberMe);
        return response;
    }

    private static class NameSplit {
        private final String firstName;
        private final String lastName;

        public NameSplit(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}