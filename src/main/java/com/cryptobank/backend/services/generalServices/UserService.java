package com.cryptobank.backend.services.user;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.services.AbstractCRUDService;
import com.cryptobank.backend.services.generalServices.EmailService;
import com.cryptobank.backend.utils.GetNotFoundThrows;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService extends AbstractCRUDService<User> {

    private final UserDAO repository;
    private final EmailService emailService;

    public UserService(UserDAO repository, EmailService emailService) {
        super(repository, User.class);
        this.repository = repository;
        this.emailService = emailService;
    }

    @Override
    public User save(User entity) {
        User user = repository.findByEmail(entity.getEmail());
        if (user != null) {
            throw new AlreadyExistException("User with email " + entity.getEmail() + " already exists");
        } else {
            return super.save(entity);
        }
    }

    @GetNotFoundThrows
    public User getEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<User> getName(String name) {
        return repository.findByName(name);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhone(phoneNumber);
    }

    public boolean existsByIdCardNumber(String cardNumber) {
        return repository.existsByIdNumber(cardNumber);
    }

    private String generateResetCode() {
        int randomCode = (int) (Math.random() * 900000) + 100000;  // Tạo mã 6 chữ số
        return String.valueOf(randomCode);
    }

    public void requestResetPassword(String email, HttpSession session) {
        getEmail(email);

        // Tạo mã xác thực ngẫu nhiên 6 chữ số
        String resetCode = generateResetCode();
        session.setAttribute("OTP", resetCode);
        session.setAttribute("time", LocalDateTime.now().plusMinutes(15));// Mã hết hạn sau 15 phút

        // Gửi email với mã xác thực
        emailService.sendResetPasswordEmail(email, resetCode);
    }

    public void resetPassword(String email, String resetCode, String newPassword, HttpSession session) {
        // Tìm người dùng qua email
        User user = getEmail(email);

        // Kiểm tra mã resetCode có đúng không
        String OTP = (String) session.getAttribute("OTP");
        if (!OTP.equals(resetCode)) {
            throw new RuntimeException("Mã xác thực không hợp lệ");
        }

        // Kiểm tra mã resetCode có hết hạn hay không
        LocalDateTime timeLimit = (LocalDateTime)session.getAttribute("time");
        if (!timeLimit.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực đã hết hạn");
        }

        // Mã xác thực hợp lệ, tiến hành thay đổi mật khẩu
        // Mã hóa mật khẩu mới
        String encodedPassword = new BCryptPasswordEncoder().encode(newPassword);
        User userChangePass=repository.findByEmail(email);
        userChangePass.setPassword(encodedPassword);

        // Lưu thông tin người dùng mới với mật khẩu đã thay đổi vào database
        super.save(userChangePass);
    }

}
