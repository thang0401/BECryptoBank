package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.UserCreateRequest;
import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.DTO.UserUpdateRequest;
import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserRole;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.UserRoleDAO;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserDAO repository;
    private final EmailService emailService;
    private final UserRoleDAO userRoleDAO;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    private UserInformation convertToUserInformation(User user) {
        UserInformation dto = new UserInformation();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
    public User getUserKYC(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found or deleted"));
    }

    private User getUserEntity(String id) {
        return repository.findById(id)
            .filter(u -> !u.getDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found or deleted"));
    }

    public UserInformation get(String id) {
        User user = getUserEntity(id);
        return convertToUserInformation(user);
    }

    public List<UserInformation> getAll() {
        return repository.findAll().stream()
                .filter(user -> !user.getDeleted())
                .map(this::convertToUserInformation)
                .collect(Collectors.toList());
    }

    public Page<UserInformation> getAll(int page, int size) {
        return repository.findAllNotDeleted(PageRequest.of(page, size))
                .map(this::convertToUserInformation);
    }

    public void delete(String id, String deletedBy) {
        User user = getUserEntity(id);
        user.setDeleted(true);
        user.setModifiedAt(OffsetDateTime.now());
        user.setModifiedBy(deletedBy);
        repository.save(user);
    }

    public UserInformation update(String id, UserUpdateRequest request, String modifiedBy) {
        User user = getUserEntity(id);
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getHomeAddress() != null) user.setHomeAddress(request.getHomeAddress());
        user.setModifiedAt(OffsetDateTime.now());
        user.setModifiedBy(modifiedBy);
        User updatedUser = repository.save(user);
        return convertToUserInformation(updatedUser);
    }

    public UserInformation save(UserCreateRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new ResourceNotFoundException("User with email " + request.getEmail() + " already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setHomeAddress(request.getHomeAddress());
        user.setDeleted(false);
        user.setCreatedAt(OffsetDateTime.now());
        User savedUser = repository.save(user);
        return convertToUserInformation(savedUser);
    }

    public UserInformation getEmail(String email) {
        User user = repository.findByEmail(email);
        if (user != null && !user.getDeleted()) {
            return convertToUserInformation(user);
        }
        throw new ResourceNotFoundException("User with email " + email + " not found or deleted");
    }

    public List<UserInformation> getName(String name) {
        return repository.findByName(name).stream()
                .filter(user -> !user.getDeleted())
                .map(this::convertToUserInformation)
                .collect(Collectors.toList());
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
        int randomCode = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(randomCode);
    }

    public void requestResetPassword(String email, HttpSession session) {
        User user = repository.findByEmail(email);
        if (user == null || user.getDeleted()) {
            throw new ResourceNotFoundException("User with email " + email + " not found or deleted");
        }
        String resetCode = generateResetCode();
        session.setAttribute("OTP", resetCode);
        session.setAttribute("time", LocalDateTime.now().plusMinutes(15));
        emailService.sendResetPasswordEmail(email, resetCode);
    }


    public void resetPassword(String email, String resetCode, String newPassword, HttpSession session) {
        User user = repository.findByEmail(email);
        if (user == null || user.getDeleted()) {
            throw new ResourceNotFoundException("User with email " + email + " not found or deleted");
        }
        String OTP = (String) session.getAttribute("OTP");
        if (!OTP.equals(resetCode)) {
            throw new RuntimeException("Mã xác thực không hợp lệ");
        }
        LocalDateTime timeLimit = (LocalDateTime) session.getAttribute("time");
        if (!timeLimit.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực đã hết hạn");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    public Double getUserBalance(String userId) {
        Optional<User> user = repository.findById(userId);
        return 0.0; // Logic cần hoàn thiện
    }

    public void decreaseUserBalance(String userId, Double amount) {
        Optional<User> userOpt = repository.findById(userId);
        // Logic cần hoàn thiện
    }

    public List<UserInformation> getUsersByRoleName(String roleName) {
        return repository.findByRole(roleName).stream()
                .filter(user -> !user.getDeleted())
                .map(this::convertToUserInformation)
                .collect(Collectors.toList());
    }

    public List<UserInformation> getUsersByRankingName(String rankingName) {
        return repository.findByRanking_NameContaining(rankingName).stream()
                .filter(user -> !user.getDeleted())
                .map(this::convertToUserInformation)
                .collect(Collectors.toList());
    }

    public List<UserInformation> getUsersByPhoneNumber(String phoneNum) {
        return repository.findByPhoneNumberContaining(phoneNum).stream()
                .filter(user -> !user.getDeleted())
                .map(this::convertToUserInformation)
                .collect(Collectors.toList());
    }

    public UserInformation getUsersByIdNumber(String idNumber) {
        User user = repository.findByIdCardNumber(idNumber);
        if (user != null && !user.getDeleted()) {
            return convertToUserInformation(user);
        }
        throw new ResourceNotFoundException("User with id number " + idNumber + " not found or deleted");
    }

    public void addRoleToUser(String id, String... roles) {
        User user = getUserEntity(id);
        for (String roleName : roles) {
            Role role = roleService.getByName(roleName);
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleDAO.save(userRole);
        }
    }
}