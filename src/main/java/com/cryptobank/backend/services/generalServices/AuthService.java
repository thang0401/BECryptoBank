package com.cryptobank.backend.services.generalServices;

<<<<<<< HEAD
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

=======
import com.cryptobank.backend.DTO.AuthResponse;
>>>>>>> 1dc3679127a9ebd69ba397065a0c251b767ed78b
import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserOtp;
import com.cryptobank.backend.repository.DeviceInforDAO;
import com.cryptobank.backend.repository.UserDAO;
<<<<<<< HEAD
import com.cryptobank.backend.repository.UserOtpRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ua_parser.Client;
import ua_parser.Device;
import ua_parser.Parser;
=======
import com.cryptobank.backend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
>>>>>>> 1dc3679127a9ebd69ba397065a0c251b767ed78b

@Service
@RequiredArgsConstructor
public class AuthService {
<<<<<<< HEAD
	@Autowired
	private UserDAO userRepository;

	@Autowired
	private DeviceInforDAO deviceInfoRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserOtpRepository userOtpRepository;

	private EmailDeviceService emailDeviceService;

	public List<Optional<DeviceInfo>> getAllDeviceFromUser(String userId) {
		return deviceInfoRepository.getAllDeviceWasLoginByUser(userId);
	}

	public Boolean login(String email, String password, HttpServletRequest request, HttpSession session) {
		try {
			User user = userRepository.findByEmail(email).orElseThrow();

			// Kiểm tra xem user có đúng không
			if (user.getUsername().isEmpty()) {
				return false;
			} else {
				// Kiểm tra xem mật khẩu nhập có đúng không
				if (user.getPassword().equals(password)) {
					// Lấy User-Agent từ request
					String userAgent = request.getHeader("User-Agent");
					Parser parser = new Parser(); // Khởi tạo UAParser
					Client client = parser.parse(userAgent);

					// Kiểm tra xem thiết bị đã tồn tại hay chưa
					Optional<DeviceInfo> existingDevice = deviceInfoRepository.findByDeviceIdAndUser(session.getId(),
							user);

					if (existingDevice.isPresent()) {
						// Nếu thiết bị đã tồn tại, kiểm tra xem có phải là thiết bị đang dùng không
						DeviceInfo device = existingDevice.get();
						if (checkDevicePresentIsInUse(user, device)) {
							return true;
						} else {
							noticationDifferentDeviceLogin(user, device);
						}
					} else {
						// Nếu là thiết bị mới, gửi thông báo xác thực
						DeviceInfo newDevice = formatToDeviceInfor(session, client, user, request);
						// Gửi cảnh báo email vì là thiết bị mới
						firstDeviceLoginNotication(user, newDevice);
					}
					return true;
				} else {
					return false;
				}
			}

		} catch (Exception e) {
			return false;
		}
	}

	private void sendNewDeviceAlert(User user, DeviceInfo device, Boolean checkContaint) {
		if (!checkContaint) {

		}
		String subject = "Cảnh báo đăng nhập từ thiết bị mới!";
		String message = String.format(
				"Xin chào %s,\n\n" + "Chúng tôi phát hiện tài khoản của bạn vừa đăng nhập từ một thiết bị mới:\n\n"
						+ "🔹 Thiết bị: %s\n" + "🔹 Hệ điều hành: %s\n" + "🔹 Trình duyệt: %s\n" + "🔹 Địa chỉ IP: %s\n"
						+ "🔹 Thời gian đăng nhập: %s\n\n"
						+ "Nếu đây không phải bạn, vui lòng đổi mật khẩu ngay lập tức hoặc liên hệ hỗ trợ!",
				user.getFirstName() + " " + user.getLastName(), device.getDeviceName(), device.getOs(),
				device.getBrowser(), device.getIpAddress(),
				device.getLastLogin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		// Gọi service gửi email
		emailDeviceService.sendEmail(user.getEmail(), subject, message);
	}

	private void firstDeviceLoginNotication(User user, DeviceInfo device) {
		String subject = "Thông báo lần đầu đăng nhập!";

		String message = String.format(
				"Xin chào %s,\n\n" + "Chúng tôi nhận thấy tài khoản của bạn là người dùng mới:\n\n"
						+ "🔹 Thiết bị: %s\n" + "🔹 Hệ điều hành: %s\n" + "🔹 Trình duyệt: %s\n" + "🔹 Địa chỉ IP: %s\n"
						+ "🔹 Thời gian đăng nhập: %s\n\n"
						+ "🔹 Đây là mã xác thực OTP cho lần đầu đăng nhập (có hiệu lực 2 phút): %s\n\n"
						+ "vui lòng dùng mã xác thực đăng nhập để trãi nghiệm CryptoBank của chúng tôi!",
				user.getFirstName() + " " + user.getLastName(), device.getDeviceName(), device.getOs(),
				device.getBrowser(), device.getIpAddress(),
				device.getLastLogin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), CreateOTP(user));
	}

	private void noticationDifferentDeviceLogin(User user, DeviceInfo device) {
		String subject = "Thông báo Đăng nhập trên thiết bị lạ!";

		String message = String.format(
				"Xin chào %s,\n\n" + "Chúng tôi nhận thấy tài khoản của bạn đang đăng nhập trên thiết bị khác:\n\n"
						+ "🔹 Thiết bị: %s\n" + "🔹 Hệ điều hành: %s\n" + "🔹 Trình duyệt: %s\n" + "🔹 Địa chỉ IP: %s\n"
						+ "🔹 Thời gian đăng nhập: %s\n\n"
						+ "🔹 Đây là mã xác thực OTP xác thực người dùng (có hiệu lực 2 phút) : %s\n\n"
						+ "Nếu đây không phải bạn vui lòng đăng nhập sau đó thay đổi mật khẩu!",
				user.getFirstName() + " " + user.getLastName(), device.getDeviceName(), device.getOs(),
				device.getBrowser(), device.getIpAddress(),
				device.getLastLogin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), CreateOTP(user));
	}

	private Boolean checkDevicePresentIsInUse(User user, DeviceInfo device) {
		Optional<DeviceInfo> deviceInused = deviceInfoRepository.findByUserAndDeviceInUse(user.getId());
		if (deviceInused.isEmpty()) {
			return false;
		} else {
			if (device.getDeviceId().equals(deviceInused.get().getDeviceId())) {
				return true;
			} else {
				return false;
			}
		}
	}

	private String CreateOTP(User user) {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		UserOtp userOtp = userOtpRepository.getById(user.getId());
		if (userOtp.getUserId().isEmpty()) {
			userOtp = new UserOtp();
			userOtp.setUser(user);
		}
		userOtp.setOtpCode(String.valueOf(otp));
		userOtp.setTimeStart(LocalDateTime.now());
		userOtp.setTimeEnd(LocalDateTime.now().plusMinutes(2));
		userOtpRepository.save(userOtp);
		return String.valueOf(otp);
	}

	public Boolean saveDeviceInforToDB(DeviceInfo deviceInfo, String OTPFromUser, HttpServletRequest request,
			HttpSession session, String userId) {
		UserOtp userOtp=userOtpRepository.getById(userId);
		if(userOtp.getTimeEnd().isAfter(LocalDateTime.now()))
		{
			if (OTPFromUser.equals(userOtp.getOtpCode())) {
				// Lấy User-Agent từ request
				String userAgent = request.getHeader("User-Agent");
				Parser parser = new Parser(); // Khởi tạo UAParser
				Client client = parser.parse(userAgent);
				
				
				deviceInfoRepository.save(deviceInfo);
				return true;
			} else {
				return false;
			}
		}
		else
		{
			
		}
		
	}

	private DeviceInfo formatToDeviceInfor(HttpSession session, Client client, User user, HttpServletRequest request) {

		DeviceInfo newDevice = new DeviceInfo();
		newDevice.setDeviceId(session.getId());
		newDevice.setDeviceName(client.device.family);
		newDevice.setOs(client.os.family + " " + client.os.major);
		newDevice.setBrowser(client.userAgent.family + " " + client.userAgent.major);
		newDevice.setIpAddress(request.getRemoteAddr());
		newDevice.setLastLogin(LocalDateTime.now());
		newDevice.setUser(user);
		newDevice.setInUse(true);
		return newDevice;
	}

	public void logout(HttpSession session) {
		session.invalidate();
	}
=======

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

>>>>>>> 1dc3679127a9ebd69ba397065a0c251b767ed78b
}
