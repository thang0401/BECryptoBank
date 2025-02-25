package com.cryptobank.backend.services.generalServices;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.DeviceInforDAO;
import com.cryptobank.backend.repository.UserDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ua_parser.Client;
import ua_parser.Parser;

@Service
public class AuthService {
    @Autowired
    private UserDAO userRepository;

    @Autowired
    private DeviceInforDAO deviceInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Boolean login(String email, String password, HttpServletRequest request, HttpSession session) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow();
            
            // Lấy User-Agent từ request
            String userAgent = request.getHeader("User-Agent");
            Parser parser = new Parser(); // Khởi tạo UAParser
            Client client = parser.parse(userAgent);

            // Lấy thông tin hệ điều hành, trình duyệt và thiết bị
            String deviceName = client.device.family; // VD: iPhone 12, Realme 9
            String os = client.os.family + " " + client.os.major; // VD: Android 14, iOS 17
            String browser = client.userAgent.family + " " + client.userAgent.major; // VD: Chrome 120

            // Lưu thông tin thiết bị vào database
            DeviceInfo device = new DeviceInfo();
            device.setDeviceId(session.getId()); 
            device.setDeviceName(deviceName);
            device.setOs(os);
            device.setBrowser(browser);
            device.setIpAddress(request.getRemoteAddr()); 
            device.setLastLogin(LocalDateTime.now());
            device.setUser(user);

            deviceInfoRepository.save(device);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
