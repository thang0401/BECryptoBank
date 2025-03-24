package com.cryptobank.backend.services;

import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserRole;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Từ nhánh bị conflict
    public boolean checkUserAuthenticated(HttpSession session) {
        return true;
    }

    // Từ nhánh bị conflict
    public User getUserId(String id) {
        User customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer '" + id + "' not found"));
        return customer;
    }

    // Từ nhánh main
    public User get(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User id " + id + " not found"));
    }

    // Từ nhánh bị conflict
    public List<User> getUserName(String name) {
        List<User> customers = repository.findByName(name);
        if (customers.isEmpty())
            throw new ResourceNotFoundException("customer '" + name + "' not found");
        return customers;
    }

    // Từ nhánh main
    public List<User> getName(String name) {
        return repository.findByName(name);
    }

    // Từ nhánh bị conflict
//    public User getUserEmail(String email) {
//        User customer = repository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("customer '" + email + "' not found"));
//        return customer;
//    }

    // Từ nhánh main
    public User getEmail(String email) {
        return repository.findByEmail(email);
    }

    // Từ nhánh bị conflict
    public List<User> Users() {
        return repository.findAll();
    }

    // Từ nhánh main
    public List<User> getAll() {
        return repository.findAll();
    }

    // Từ nhánh bị conflict (giữ lại thay vì getAll(int, int) từ nhánh main)
    public Page<User> getAllUsers(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    // Từ nhánh bị conflict
//    public User createUser(User customer) {
//        repository.findByEmail(customer.getEmail())
//            .ifPresentOrElse(
//                c -> {
//                    throw new AlreadyExistException("customer '" + customer.getEmail() + "' already exist");
//                },
//                () -> {
//                    // TODO
//                    // create address, role in db if not exist,
//                    // create default portfolio for new customer
//                    // before save
//                    customer.setPassword(passwordEncoder.encode(customer.getPassword()));
//                    repository.save(customer);
//                }
//            );
//        return customer;
//    }

    // Từ nhánh main
    public User save(User entity) {
        User user = repository.findByEmail(entity.getEmail());
        if (user != null) {
            throw new RuntimeException("User with email " + entity.getEmail() + " already exists");
        } else {
            return repository.save(entity);
        }
    }

    // Từ nhánh bị conflict
//    public User updateUser(String id, User customer) {
//        User currentCustomer = getUserId(id);
//        User newCustomer = updateExistingCustomer(currentCustomer, customer);
//        return repository.save(newCustomer);
//    }

    // Từ nhánh main
    public void update(String id, User user) {
        if (repository.existsById(id)) {
            repository.save(user);
        }
    }

    // Từ nhánh bị conflict
//    public void deleteUser(String id) {
//        repository.findById(id)
//                .ifPresentOrElse(repository::delete, () -> {
//                    throw new ResourceNotFoundException("customer '" + id + "' not found");
//                });
//    }

    // Từ nhánh main
    public void delete(String id) {
        User user = get(id);
        repository.delete(user);
    }

    // Từ nhánh main
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    // Từ nhánh main
    public boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhoneNumber(phoneNumber);
    }

    // Từ nhánh main
    public boolean existsByIdCardNumber(String cardNumber) {
        return repository.existsByIdCardNumber(cardNumber);
    }

    // Từ nhánh main
    private String generateResetCode() {
        int randomCode = (int) (Math.random() * 900000) + 100000;  // Tạo mã 6 chữ số
        return String.valueOf(randomCode);
    }

    // Từ nhánh main
    public void requestResetPassword(String email, HttpSession session) {
        getEmail(email);

        String resetCode = generateResetCode();
        session.setAttribute("OTP", resetCode);
        session.setAttribute("time", LocalDateTime.now().plusMinutes(15));
        emailService.sendResetPasswordEmail(email, resetCode);
    }

    // Từ nhánh main
    public void resetPassword(String email, String resetCode, String newPassword, HttpSession session) {
        User user = getEmail(email);

        String OTP = (String) session.getAttribute("OTP");
        if (!OTP.equals(resetCode)) {
            throw new RuntimeException("Mã xác thực không hợp lệ");
        }

        LocalDateTime timeLimit = (LocalDateTime) session.getAttribute("time");
        if (!timeLimit.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực đã hết hạn");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        User userChangePass = repository.findByEmail(email);
        userChangePass.setPassword(encodedPassword);
        save(userChangePass);
    }

    // Từ nhánh main
    public Double getUserBalance(String userId) {
        Optional<User> user = repository.findById(userId);
        return 0.0; // Logic bị comment trong code gốc
    }

    // Từ nhánh main
    public void decreaseUserBalance(String userId, Double amount) {
        Optional<User> userOpt = repository.findById(userId);
        // Logic bị comment trong code gốc
    }

    // Từ nhánh main
    public List<User> getUsersByRoleName(String roleName) {
        return repository.findByRole(roleName);
    }

    // Từ nhánh main
    public List<User> getUsersByRankingName(String rankingName) {
        return repository.findByRanking_NameContaining(rankingName);
    }

    // Từ nhánh main
    public List<User> getUsersByPhoneNumber(String phoneNum) {
        return repository.findByPhoneNumberContaining(phoneNum);
    }

    // Từ nhánh main
    public User getUsersByIdNumber(String idNumber) {
        return repository.findByIdCardNumber(idNumber);
    }

    // Từ nhánh main
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

    // Từ nhánh bị conflict
//    private User updateExistingCustomer(User current, User newUser) {
//        return current.builder()
//                .firstName(newUser.getFirstName())
//                .lastName(newUser.getLastName())
//                .phone(newUser.getPhone())
//                .gender(newUser.getGender())
//                .avatarURL(newUser.getAvatarURL())
//                .status(newUser.getStatus())
//                .password(passwordEncoder.encode(newUser.getPassword()))
//                .smartOTP(newUser.getSmartOTP())
//                .idNumber(newUser.getIdNumber())
//                .googleId(newUser.getGoogleId())
//                .dateOfBirth(newUser.getDateOfBirth())
//                .idCardFrontImgURL(newUser.getIdCardFrontImgURL())
//                .idCardBackImgURL(newUser.getIdCardBackImgURL())
//                .activated(newUser.isActivated())
//                .deleted(newUser.isDeleted())
//                .modifiedDate(newUser.getModifiedDate())
//                .modifiedBy(newUser.getModifiedBy())
//                .build();
//    }
}