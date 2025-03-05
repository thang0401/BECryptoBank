package com.cryptobank.backend.services.generalServices;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

import org.aspectj.weaver.ast.Not;
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
import ua_parser.Device;
import ua_parser.Parser;

@Service
public class AuthService {
    @Autowired
    private UserDAO userRepository;

    @Autowired
    private DeviceInforDAO deviceInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private String OtpDevice="";
    
    private EmailDeviceService emailDeviceService;

    public Boolean login(String email, String password, HttpServletRequest request, HttpSession session) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow();
            
            // Kiểm tra xem user có đúng không
            if(user.getUsername().isEmpty())
            {
            	return false;
            }
            else
            {
            	if(user.getPassword().equals(password))
            	{
            		 // Lấy User-Agent từ request
                    String userAgent = request.getHeader("User-Agent");
                    Parser parser = new Parser(); // Khởi tạo UAParser
                    Client client = parser.parse(userAgent);

//                    // Lấy thông tin hệ điều hành, trình duyệt và thiết bị
//                    String deviceName = client.device.family;
//                    String os = client.os.family + " " + client.os.major;
//                    String browser = client.userAgent.family + " " + client.userAgent.major;
//                    String ipAddress = request.getRemoteAddr();
//                    String sessionId = session.getId();

                    // Kiểm tra xem thiết bị đã tồn tại hay chưa
                    Optional<DeviceInfo> existingDevice = deviceInfoRepository.findByDeviceIdAndUser(session.getId(), user);

                    if (existingDevice.isPresent()) {
                        // Nếu thiết bị đã tồn tại, kiểm tra xem có phải là thiết bị đang dùng không
                        DeviceInfo device = existingDevice.get();
                    	checkDevicePresentIsInUse(user, device);
                    	
//                        DeviceInfo device = existingDevice.get();
//                        device.setLastLogin(LocalDateTime.now());
//                        deviceInfoRepository.save(device);
                    } else {
                        // Nếu là thiết bị mới, gửi thông báo xác thực 
                    	DeviceInfo newDevice = formatToDeviceInfor(session, client, user, request);
                    	 //  Gửi cảnh báo email vì là thiết bị mới
                    	firstDeviceLoginNotication(user, newDevice);
                    	
                        
//                        deviceInfoRepository.save(newDevice);

                       
                        
                    }
                    return true;
            	}
            	else
            	{
            		return false;
            	}
            }
           
        } catch (Exception e) {
            return false;
        }
    }

    private void sendNewDeviceAlert(User user, DeviceInfo device,Boolean checkContaint) {
    	if(!checkContaint)
    	{
    		
    	}
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
    
    private void firstDeviceLoginNotication(User user,DeviceInfo device)
    {
    	String subject = "Thông báo lần đầu đăng nhập!";
    	
        String message = String.format(
            "Xin chào %s,\n\n" +
            "Chúng tôi nhận thấy tài khoản của bạn là người dùng mới:\n\n" +
            "🔹 Thiết bị: %s\n" +
            "🔹 Hệ điều hành: %s\n" +
            "🔹 Trình duyệt: %s\n" +
            "🔹 Địa chỉ IP: %s\n" +
            "🔹 Thời gian đăng nhập: %s\n\n" +
            "🔹 Đây là mã xác thực OTP cho lần đầu đăng nhập (có hiệu lực 1 phút): %s\n\n"+
            "vui lòng dùng mã xác thực đăng nhập để trãi nghiệm CryptoBank của chúng tôi!",
            user.getFirstName()+" "+user.getLastName(),
            device.getDeviceName(),
            device.getOs(),
            device.getBrowser(),
            device.getIpAddress(),
            device.getLastLogin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            CreateOTP()
        		 );
    }
    
    private void noticationDifferentDeviceLogin(User user,DeviceInfo device)
    {
    	String subject = "Thông báo Đăng nhập trên thiết bị lạ!";
    	
        String message = String.format(
            "Xin chào %s,\n\n" +
            "Chúng tôi nhận thấy tài khoản của bạn đang đăng nhập trên thiết bị khác:\n\n" +
            "🔹 Thiết bị: %s\n" +
            "🔹 Hệ điều hành: %s\n" +
            "🔹 Trình duyệt: %s\n" +
            "🔹 Địa chỉ IP: %s\n" +
            "🔹 Thời gian đăng nhập: %s\n\n" +
            "🔹 Đây là mã xác thực OTP xác thực người dùng : %s\n\n"+
            "Nếu đây không phải bạn vui lòng đăng nhập sau đó thay đổi mật khẩu!",
            user.getFirstName()+" "+user.getLastName(),
            device.getDeviceName(),
            device.getOs(),
            device.getBrowser(),
            device.getIpAddress(),
            device.getLastLogin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            CreateOTP()
        		 );
    }
    
    private Boolean checkDevicePresentIsInUse(User user,DeviceInfo device)
    {
    	Optional<DeviceInfo> deviceInused=deviceInfoRepository.findByUserAndDeviceInUse(user.getId());
    	if(deviceInused.isEmpty())
    	{
    		
    		return false;
    	}
    	else
    	{
    		if(device.getDeviceId().equals(deviceInused.get().getDeviceId()))
    		{
    			return true;
    		}
    		else
    		{
    			
    		}
    		noticationDifferentDeviceLogin(user, device);
    	}
    }
    
    private String CreateOTP()
    {
    	 Random random = new Random();
         int otp = 100000 + random.nextInt(900000);
         OtpDevice=String.valueOf(otp);
         return String.valueOf(otp);
    }
    
    private Void saveDeviceInforToDB(DeviceInfo deviceInfo,String OTPFromUser)
    {
    	if(OtpDevice.equals(OTPFromUser))
    	{
    		
    	}
    }
    
    private DeviceInfo formatToDeviceInfor(HttpSession session,Client client,User user, HttpServletRequest request)
    {
    	
    	DeviceInfo newDevice = new DeviceInfo();
        newDevice.setDeviceId(session.getId());
        newDevice.setDeviceName(client.device.family);
        newDevice.setOs(client.os.family + " " + client.os.major);
        newDevice.setBrowser(client.userAgent.family + " " + client.userAgent.major);
        newDevice.setIpAddress(request.getRemoteAddr());
        newDevice.setLastLogin(LocalDateTime.now());
        newDevice.setUser(user);
        
        return newDevice;
    }


    public void logout(HttpSession session) {
        session.invalidate();
    }
}
