package com.cryptobank.backend.services.generalServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	 @Autowired
	 private JavaMailSender javaMailSender;
	 
	 public void sendResetPasswordEmail(String toEmail, String resetCode) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(toEmail);
	        message.setSubject("Mã xác thực để thay đổi mật khẩu");
	        message.setText("Mã xác thực của bạn là: " + resetCode + "\nMã sẽ hết hạn sau 15 phút.");
	        
	        javaMailSender.send(message);
	    }
}
