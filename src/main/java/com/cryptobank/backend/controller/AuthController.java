package com.cryptobank.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.NameSplit;
import com.cryptobank.backend.DTO.RegisterRequest;
import com.cryptobank.backend.DTO.ResetPasswordRequest;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.services.generalServices.AuthService;
import com.cryptobank.backend.services.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/Auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private UserService userService;

	private PasswordEncoder passwordEncoder;

	// Constructor
	public AuthController() {
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	// Phương thức mã hóa mật khẩu
	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	// Đăng nhập
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password,
			HttpServletRequest request, HttpSession session) {
		// Gọi phương thức login từ AuthService để xử lý logic đăng nhập
		if (authService.login(email, password, request, session)) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Đăng nhập thành công");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Đăng nhập thất bại");
		}
	}

	// Đăng xuất
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session) {
		// Gọi phương thức logout từ AuthService để xử lý logic đăng xuất
		authService.logout(session);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Đăng xuất thành công");
	}

	// Đăng ký
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
		// Kiểm tra xem email đã tồn tại chưa
		if (userService.existsByEmail(registerRequest.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được đăng ký");
		}

		// Kiểm tra xem số điện thoại đã tồn tại chưa
		if (userService.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được đăng ký");
		}

		// Kiểm tra xem số CMND đã tồn tại chưa
		if (userService.existsByIdCardNumber(registerRequest.getIdCardNumber())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số CMND đã được đăng ký");
		}

		// Tạo đối tượng người dùng và lưu vào cơ sở dữ liệu
		User user = new User();
		user.setEmail(registerRequest.getEmail());
		user.setFirstName(getFirstAndLastName(registerRequest.getFullname()).getFirstName());
		user.setLastName(getFirstAndLastName(registerRequest.getFullname()).getLastname());
		user.setPhone(registerRequest.getPhoneNumber());
		user.setIdNumber(registerRequest.getIdCardNumber());
		user.setPassword(encodePassword(registerRequest.getPassword())); // Mã hóa mật khẩu

		// Lưu người dùng vào cơ sở dữ liệu
		userService.createUser(user);

		return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công");
	}

	// Yêu cầu quên mật khẩu
	@PostMapping("/forgot-password")
	public ResponseEntity<?> requestResetPassword(@RequestBody String email, HttpSession session) {
		try {
			userService.requestResetPassword(email, session);
			return ResponseEntity.ok("Mã xác thực đã được gửi đến email của bạn");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Đổi mật khẩu
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

		// Gán firstName là phần cuối cùng
		String firstName = nameParts[nameParts.length - 1];

		// Gán lastName là tất cả các phần trước firstName
		String lastName = "";
		for (int i = 0; i < nameParts.length - 1; i++) {
			lastName += nameParts[i] + " ";
		}
		lastName = lastName.trim(); // Loại bỏ khoảng trắng thừa ở cuối

		return new NameSplit(firstName, lastName);
	}
}
