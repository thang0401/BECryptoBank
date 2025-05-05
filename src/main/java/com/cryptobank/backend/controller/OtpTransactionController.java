package com.cryptobank.backend.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.OtpRequestDTO;
import com.cryptobank.backend.DTO.SendOtpDTO;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserOtp;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.UserOtpRepository;
import com.cryptobank.backend.services.AuthService;
import com.cryptobank.backend.services.EmailDeviceService;
import com.cryptobank.backend.services.EmployeeService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/otpTransaction")
public class OtpTransactionController {
	
	private final JavaMailSender mailSender;
    private final EmailDeviceService emailDeviceService;
    private final UserOtpRepository userOtpRepository;
    private final UserDAO userDAO;
    private final AuthService authService;
	
	@PostMapping("/sendOtpCode")
	public ResponseEntity<?> sendOtpCode(@RequestBody SendOtpDTO otpUser)
	{
		try {
			Integer otpCode = 100000 + new Random().nextInt(900000);
			User user=userDAO.getById(otpUser.getUserId());
			String otpString=otpCode.toString();
			sendTransactionOtpEmail(user,otpString);
			UserOtp userOtp;
            if(userOtpRepository.findByUser(user).isEmpty())
            {
            	 userOtp = new UserOtp();
                 userOtp.randomId();
                 userOtp.setUser(user);
                 userOtp.setOtpCode(String.valueOf(otpCode));
                 LocalDateTime startTime = LocalDateTime.now();
                 LocalDateTime endTime = startTime.plusMinutes(5);
                 userOtp.setTimeStart(startTime);
                 userOtp.setTimeEnd(endTime);
                 System.out.println("Creating OTP for user: " + user.getId() + ", startTime: " + startTime + ", endTime: " + endTime);

                 UserOtp savedOtp = userOtpRepository.saveAndFlush(userOtp);
                 System.out.println("Saved OTP for user: " + user.getId() + ", savedTimeEnd: " + (savedOtp != null ? savedOtp.getTimeEnd() : "null"));
                 if (savedOtp == null) {
                     throw new RuntimeException("Failed to save OTP for user: " + user.getId());
                 }
            }
            else
            {
            	userOtp=userOtpRepository.findByUser(user).get();
                userOtp.setUser(user);
                userOtp.setOtpCode(String.valueOf(otpCode));
                LocalDateTime startTime = LocalDateTime.now();
                LocalDateTime endTime = startTime.plusMinutes(5);
                userOtp.setTimeStart(startTime);
                userOtp.setTimeEnd(endTime);
                System.out.println("Update OTP for user: " + user.getId() + ", startTime: " + startTime + ", endTime: " + endTime);

                UserOtp savedOtp = userOtpRepository.saveAndFlush(userOtp);
                System.out.println("Saved OTP for user: " + user.getId() + ", savedTimeEnd: " + (savedOtp != null ? savedOtp.getTimeEnd() : "null"));
                if (savedOtp == null) {
                    throw new RuntimeException("Failed to save OTP for user: " + user.getId());
                }
            }
			return ResponseEntity.ok(otpString);
		} catch (Exception e) {
			System.out.println("Lỗi khi gửi mail mã otp xác thực"+e.toString());
			return ResponseEntity.badRequest().body("Lỗi trong quá trình tạo mã otp");
		}
	}
	
	@PostMapping("/CheckOtp")
	public ResponseEntity<?> checkOtp(@RequestBody OtpRequestDTO userCheck)
	{	
		try {
			UserOtp user=userOtpRepository.getByUserId(userCheck.getUserId()).get();
			if(user.getTimeEnd().isBefore(LocalDateTime.now()))
			{
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP đã hết hạn");
			}
			if(!authService.verifyOTP(user.getUser(), userCheck.getOtpCode()))
			{
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không hợp lệ");
			}
			
			return ResponseEntity.ok("Xác thực thành công");
		} catch (Exception e) {
			System.out.println(e.toString());
			return ResponseEntity.badRequest().body("Lỗi trong quá trình kiểm tra mã otp xác thực");
		}
	}
	
	public void sendTransactionOtpEmail(User user, String otp) {
	    if (user == null || user.getId() == null || user.getEmail() == null) {
	        System.err.println("Error: Invalid user data for sending transaction OTP");
	        throw new IllegalArgumentException("User or user email cannot be null");
	    }
	    try {
	        String subject = "Mã OTP Xác Thực Giao Dịch";
	        String content = String.format("""
	                <html>
	                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
	                    <h3>Xin chào %s,</h3>
	                    <p>Vui lòng sử dụng mã OTP dưới đây để xác thực giao dịch của bạn:</p>
	                    <ul>
	                        <li><strong>Mã OTP (hiệu lực 5 phút):</strong> <span style="font-weight: bold; color: #d32f2f;">%s</span></li>
	                        <li><strong>Thời gian:</strong> %s</li>
	                    </ul>
	                    <p>Nếu bạn không thực hiện giao dịch này, vui lòng liên hệ hỗ trợ ngay!</p>
	                </body>
	                </html>
	                """,
	                user.getFullName() != null ? user.getFullName() : "User",
	                otp,
	                OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

	        sendEmail(user.getEmail(), subject, content, true); // true để chỉ định nội dung là HTML
	        System.out.println("Sent transaction OTP email for user: " + user.getId() + ", email: " + user.getEmail());
	    } catch (Exception e) {
	        System.err.println("Error sending transaction OTP email for user: " + user.getId() + ", message: " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("Failed to send transaction OTP email", e);
	    }
	}
	
	public void sendEmail(String to, String subject, String text, boolean isHtml) {
	    try {
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(text, isHtml); // isHtml = true để gửi nội dung HTML
	        helper.setFrom("phongpvps36848@fpt.edu.vn"); 

	        mailSender.send(mimeMessage);
	        System.out.println("Email sent successfully to: " + to);
	    } catch (Exception e) {
	        System.err.println("Error sending email to: " + to + ", message: " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("Failed to send email", e);
	    }
	}
}
