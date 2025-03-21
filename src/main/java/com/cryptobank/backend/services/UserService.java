package com.cryptobank.backend.services;

import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserRole;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.UserRoleDAO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserDAO repository;
    private final EmailService emailService;
    private final UserRoleDAO userRoleDAO;
    private final RoleService roleService;

    public User get(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User id " + id + " not found"));
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public Page<User> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public void delete(String id) {
        User user = get(id);
        repository.delete(user);
    }

    public void update(String id, User user) {
        if (repository.existsById(id)) {
            repository.save(user);
        }
    }

    public User save(User entity) {
        User user = repository.findByEmail(entity.getEmail());
        if (user != null) {
            throw new RuntimeException("User with email " + entity.getEmail() + " already exists");
        } else {
            return repository.save(entity);
        }
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
        save(userChangePass);
    }

 // Lấy số dư USDC của user
 	public Double getUserBalance(String userId) {
 		Optional<User> user = repository.findById(userId);
 		// return user.map(User::getUsdcBalance).orElse(0.0);
 		return 0.0;
 	}

 	// Giảm số dư USDC sau khi rút tiền thành công
 	public void decreaseUserBalance(String userId, Double amount) 
 	{
 		Optional<User> userOpt = repository.findById(userId);
// 		userOpt.ifPresent(user -> {
// 			user.setUsdcBalance(user.getUsdcBalance() - amount);
// 			repository.save(user);
// 		});
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

    public void addRoleToUser(String id, String... roles) {
        User user = get(id);
        for (String roleName : roles) {
            Role role = roleService.getByName(roleName);
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleDAO.save(userRole);
        }
    }

}
