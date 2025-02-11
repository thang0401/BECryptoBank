package com.cryptobank.backend.services.generalServices;

import java.time.LocalDateTime;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.DeviceInforDAO;
import com.cryptobank.backend.repository.UserDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {
	 	@Autowired
	    private UserDAO userRepository;
	    
	    @Autowired
	    private DeviceInforDAO deviceInfoRepository;
	    
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    
//	    @Autowired
//	    private AuthenticationManager authenticationManager;
	    
	    public Boolean login(String email, String password, HttpServletRequest request, HttpSession session) {
//	        Authentication authentication = authenticationManager.authenticate(
//	            new UsernamePasswordAuthenticationToken(email, password)
//	        );
//	        SecurityContextHolder.getContext().setAuthentication(authentication);

	    	try {
	    		User user = userRepository.findByEmail(email).orElseThrow();

		        // **Lưu thông tin thiết bị vào database**
		        DeviceInfo device = new DeviceInfo();
		        device.setDeviceId(session.getId()); // Lưu sessionId thay vì token
		        device.setDeviceName(request.getHeader("User-Agent")); // Lấy thông tin thiết bị từ User-Agent
		        device.setIpAddress(request.getRemoteAddr()); // Lấy địa chỉ IP
		        device.setLastLogin(LocalDateTime.now());
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
}
