package com.cryptobank.backend.services.generalServices;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    
    private EmailDeviceService emailDeviceService;

    public Boolean login(String email, String password, HttpServletRequest request, HttpSession session) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow();

            // Lấy User-Agent từ request
            String userAgent = request.getHeader("User-Agent");
            Parser parser = new Parser(); // Khởi tạo UAParser
            Client client = parser.parse(userAgent);

            // Lấy thông tin hệ điều hành, trình duyệt và thiết bị
            String deviceName = client.device.family;
            String os = client.os.family + " " + client.os.major;
            String browser = client.userAgent.family + " " + client.userAgent.major;
            String ipAddress = request.getRemoteAddr();
            String sessionId = session.getId();

            // Kiểm tra xem thiết bị đã tồn tại hay chưa
            Optional<DeviceInfo> existingDevice = deviceInfoRepository.findByDeviceIdAndUser(sessionId, user);

            if (existingDevice.isPresent()) {
                // Nếu thiết bị đã tồn tại, chỉ cập nhật ngày truy cập
                DeviceInfo device = existingDevice.get();
                device.setLastLogin(LocalDateTime.now());
                deviceInfoRepository.save(device);
            } else {
                // Nếu là thiết bị mới, thêm mới vào DB
                DeviceInfo newDevice = new DeviceInfo();
                newDevice.setDeviceId(sessionId);
                newDevice.setDeviceName(deviceName);
                newDevice.setOs(os);
                newDevice.setBrowser(browser);
                newDevice.setIpAddress(ipAddress);
                newDevice.setLastLogin(LocalDateTime.now());
                newDevice.setUser(user);

                deviceInfoRepository.save(newDevice);

                //  Gửi cảnh báo email vì là thiết bị mới
                sendNewDeviceAlert(user, newDevice);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void sendNewDeviceAlert(User user, DeviceInfo device) {
        String subject = "Cảnh báo đăng nhập từ thiết bị mới!";
        String message = String.format(
            "Xin chào %s,\n\n" +
            "Chúng tôi phát hiện tài khoản của bạn vừa đăng nhập từ một thiết bị mới:\n\n" +
            "🔹 Thiết bị: %s\n" +
            "🔹 Hệ điều hành: %s\n" +
            "🔹 Trình duyệt: %s\n" +
            "🔹 Địa chỉ IP: %s\n" +
            "🔹 Thời gian đăng nhập: %s\n\n" +
            "Nếu đây không phải bạn, vui lòng đổi mật khẩu ngay lập tức hoặc liên hệ hỗ trợ!",
            user.getFirstName()+" "+user.getLastName(),
            device.getDeviceName(),
            device.getOs(),
            device.getBrowser(),
            device.getIpAddress(),
            device.getLastLogin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        // Gọi service gửi email
        emailDeviceService.sendEmail(user.getEmail(), subject, message);
    }


    public void logout(HttpSession session) {
        session.invalidate();
    }
}
