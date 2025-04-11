package com.cryptobank.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cryptobank.backend.DTO.*;
import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.services.AuthService;
import com.cryptobank.backend.services.UserService;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;

    private PasswordEncoder passwordEncoder;

    public AuthController() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
 
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestAuth login, HttpServletRequest request, HttpSession session) {
        try {
            AuthResponse response = authService.loginWithEmail(login.getEmail(), login.getPassword(), login.isRememberMe(), request, session);
            return ResponseEntity.ok(response);
        } catch (com.cryptobank.backend.exception.AuthException e) {
            if ("OTP verification required".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Đưa đến trang nhập mã OTP xác thực");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Đăng nhập thất bại: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(@RequestParam String idToken, HttpServletRequest request,
            HttpSession session) {
        try {
            AuthResponse response = authService.loginWithGoogle(idToken, request, session);
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

    @PostMapping("/login/OTP")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request, HttpSession session, HttpServletRequest servletRequest) {
        try {
            if (authService.saveDeviceAfterOtp(request.getOtp(), servletRequest, session,request.getUser_id())) {
                User user = authService.getUserById(request.getUser_id());
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
                }
                String email = user.getEmail();
                String accessToken = authService.getJwtUtil().generateAccessToken(email);
                String refreshToken = request.isRememberMe() ? 
                    authService.getJwtUtil().generateRefreshToken(email, 30 * 24 * 60 * 60) : 
                    authService.getJwtUtil().generateRefreshToken(email);
                AuthResponse response = new AuthResponse(email, accessToken, refreshToken);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Xác Thực OTP Thất bại");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP verification failed: " + e.getMessage());
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
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request, HttpSession session) {
        try {
            userService.resetPassword(request.getEmail(), request.getResetCode(), request.getNewPassword(), session);
            return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private static NameSplit getFirstAndLastName(String fullname) {
        String[] nameParts = fullname.split("\\s+");
        if (nameParts.length == 1) {
            System.out.println("First Name: " + nameParts[0]);
            return new NameSplit(nameParts[0], "");
        }
        String firstName = nameParts[nameParts.length - 1];
        String lastName = "";
        for (int i = 0; i < nameParts.length - 1; i++) {
            lastName += nameParts[i] + " ";
        }
        lastName = lastName.trim();
        return new NameSplit(firstName, lastName);
    }

    @GetMapping("/getAllDevice/{userId}")
    public ResponseEntity<List<Optional<DeviceInfo>>> getAllDeviceFromUser(@PathVariable String userId) {
        List<Optional<DeviceInfo>> listDevice = authService.getAllDeviceFromUser(userId);
        return ResponseEntity.ok(listDevice);
    }

    @PostMapping("/accesstoken")
    public AuthResponse getAccessToken(@RequestParam String username, @RequestParam String password) {
        return authService.authenticate(username, password);
    }
}