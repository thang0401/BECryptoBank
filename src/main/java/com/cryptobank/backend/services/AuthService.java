package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.AuthResponse;
import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.DeviceInforDAO;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserDAO userRepository;
	private final DeviceInforDAO deviceInfoRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public Boolean login(String email, String password, HttpServletRequest request, HttpSession session) {
//	        Authentication authentication = authenticationManager.authenticate(
//	            new UsernamePasswordAuthenticationToken(email, password)
//	        );
//	        SecurityContextHolder.getContext().setAuthentication(authentication);

		try {
			User user = userRepository.findByEmail(email);

			// **Lưu thông tin thiết bị vào database**
			DeviceInfo device = new DeviceInfo();
			device.setDeviceId(session.getId()); // Lưu sessionId thay vì token
			device.setDeviceName(request.getHeader("User-Agent")); // Lấy thông tin thiết bị từ User-Agent
			device.setIpAddress(request.getRemoteAddr()); // Lấy địa chỉ IP
			device.setLastLoginAt(OffsetDateTime.now());
			device.setUser(user);

			deviceInfoRepository.save(device);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void logout(HttpSession session) {
		session.invalidate(); // Xóa session khi logout
	}

	public AuthResponse authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		String accessToken = jwtUtil.generateAccessToken(username);
		String refreshToken = jwtUtil.generateRefreshToken(password);
		return new AuthResponse("", accessToken, refreshToken);
	}

}
