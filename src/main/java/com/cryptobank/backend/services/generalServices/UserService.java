package com.cryptobank.backend.services.generalServices;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.UserDAO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserDAO repository;
    private final EmailService emailService;

    // Tìm kiếm user chưa bị xóa
    public User get(String id) {
        return repository.findById(id)
                .filter(user -> !user.getDeleted())
                .orElseThrow(() -> new RuntimeException("User id " + id + " not found or deleted"));
    }

    public List<User> getAll() {
        return repository.findAll().stream()
                .filter(user -> !user.getDeleted())
                .toList();
    }
    
    public Page<User> getAll(int page, int size) {
        return (Page<User>) repository.findAll(PageRequest.of(page, size))
                .filter(user -> !user.getDeleted());
    }

 // Xóa mềm: Cập nhật delete_yn, modified_at, modified_by
    public void delete(String id, String deletedBy) {
        User user = get(id);
        user.setDeleted(true);
        user.setModifiedAt(OffsetDateTime.now());
        user.setModifiedBy(deletedBy);
        repository.save(user);
    }

    public void update(String id, User user, String modifiedBy) {
        User existingUser = get(id); // Chỉ lấy user chưa xóa
        BeanUtils.copyProperties(user, existingUser, "id", "deleted", "createdAt", "createdBy"); // Sao chép, bỏ qua các trường không muốn cập nhật
        existingUser.setModifiedAt(OffsetDateTime.now());
        existingUser.setModifiedBy(modifiedBy);
        repository.save(existingUser);
    }

    public User save(User entity) {
        if (repository.existsByEmail(entity.getEmail())) {
            throw new RuntimeException("User with email " + entity.getEmail() + " already exists");
        }
        entity.setDeleted(false); // Đảm bảo user mới không bị xóa
        entity.setCreatedAt(OffsetDateTime.now());
        return repository.save(entity);
    }
    
    public List<User> getUsersByRoleName(String roleName) {
        return repository.findByRole(roleName);
    }

    public List<User> getUsersByRankingName(String rankingName) {
        return repository.findByRanking_NameContaining(rankingName);
    }

    public List<User> getUsersByPhoneNumber(String phoneNum) {
        return repository.findByPhoneNumberContaining(phoneNum);
    }

    public User getUsersByIdNumber(String idNumber) {
        return repository.findByIdCardNumber(idNumber);
    }

    public List<User> getUserByUserName(String name) {
        return repository.findByName(name);
    }

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
        return repository.existsByPhoneNumber(phoneNumber);
    }

    public boolean existsByIdCardNumber(String cardNumber) {
        return repository.existsByIdCardNumber(cardNumber);
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
        if (user == null || user.getDeleted()) {
            throw new RuntimeException("User with email " + email + " not found or deleted");
        }

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
//        userChangePass.setPassword(encodedPassword);

        // Lưu thông tin người dùng mới với mật khẩu đã thay đổi vào database
        save(userChangePass);
    }

}
