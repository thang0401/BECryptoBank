package com.cryptobank.backend.services;

import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.DeviceInforDAO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailDeviceService {
	private final JavaMailSender mailSender;
	private final DeviceInforDAO deviceInfoRepository;

	public EmailDeviceService(JavaMailSender mailSender, DeviceInforDAO deviceInfoRepository) {
		this.mailSender = mailSender;
		this.deviceInfoRepository = deviceInfoRepository;
	}

	public void sendEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);
			System.out.println("Email sent successfully to: " + to);
		} catch (Exception e) {
			System.err.println("Error sending email to: " + to + ", message: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to send email", e);
		}
	}

	public void sendOtpEmail(User user, DeviceInfo deviceInfo, String otp) {
		if (user == null || user.getId() == null || user.getEmail() == null) {
			System.err.println("Error: Invalid user data for sending OTP");
			throw new IllegalArgumentException("User or user email cannot be null");
		}
		if (deviceInfo == null) {
			System.err.println("Error: DeviceInfo is null for sending OTP");
			throw new IllegalArgumentException("DeviceInfo cannot be null");
		}
		try {
			String subject = "Đăng nhập từ thiết bị khác!?";
			String content = String.format("""
                    Xin chào %s,

                    Tài khoản của bạn đang đăng nhập từ thiết bị khác:

                    🔹 Thiết bị: %s
                    🔹 Hệ điều hành: %s
                    🔹 Trình duyệt: %s
                    🔹 IP: %s
                    🔹 Thời gian: %s

                    🔹 Mã OTP (hiệu lực 5 phút): %s

                    Nếu không phải bạn, vui lòng đổi mật khẩu ngay!
                    """, user.getFullName() != null ? user.getFullName() : "User",
					deviceInfo.getDeviceName() != null ? deviceInfo.getDeviceName() : "Unknown Device",
					deviceInfo.getOs(), deviceInfo.getBrowser(), deviceInfo.getIpAddress(),
					OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), otp);
			sendEmail(user.getEmail(), subject, content);
			System.out.println("Sent OTP email for user: " + user.getId() + ", email: " + user.getEmail());
		} catch (Exception e) {
			System.err.println("Error sending OTP email for user: " + user.getId() + ", message: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to send OTP email", e);
		}
	}

	public DeviceInfo updateDeviceInfo(DeviceInfo deviceInfo, String userId) {
		if (deviceInfo == null || userId == null) {
			System.err.println("Error: Invalid deviceInfo or userId in updateDeviceInfo");
			throw new IllegalArgumentException("DeviceInfo or userId cannot be null");
		}
		try {
			System.out.println("Updating device info for user: " + userId + ", device: " + deviceInfo.getDeviceName());
			// Note: Cannot use authService.getUserById here due to circular dependency
			// Assume deviceInfo already has user set, or fetch from repository if needed
			deviceInfo.setLastLoginAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
			deviceInfo.setInUse(true);
			DeviceInfo savedDevice = deviceInfoRepository.save(deviceInfo);
			System.out.println("Saved device info for user: " + userId + ", deviceId: " + savedDevice.getDeviceId());
			return savedDevice;
		} catch (Exception e) {
			System.err.println("Error updating device info for user: " + userId + ", message: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to update device info", e);
		}
	}
}