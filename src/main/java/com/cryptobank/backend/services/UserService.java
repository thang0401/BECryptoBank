package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.UserCreateRequest;
import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.DTO.UserUpdateRequest;
import com.cryptobank.backend.DTO.request.UserSearchParamRequest;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserRole;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.mapper.UserMapper;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.UserRoleDAO;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserDAO repository;
    private final EmailService emailService;
    private final UserRoleDAO userRoleDAO;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final StatusService statusService;

    public UserInformation convertToUserInformation(User user) {
        UserInformation dto = new UserInformation();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    public User getUserEntity(String id) {
        return repository.findOne(ignoreDeleted().and((root, query, cb) -> cb.equal(root.get("id"), id)))
            .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found or deleted"));
    }

    public UserInformation get(String id) {
        User user = getUserEntity(id);
        return convertToUserInformation(user);
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
        user.setStatus(statusService.getById("cvvvhlbme6nnaun2s4qg"));
        User savedUser = repository.save(user);
        return convertToUserInformation(savedUser);
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

    public Double getUserBalance(String id) {
        User user = getUserEntity(id);
        return 0.0; // Logic cần hoàn thiện
    }

    public void decreaseUserBalance(String id, Double amount) {
        User userOpt = getUserEntity(id);
        // Logic cần hoàn thiện
    }

    public Optional<UserRole> getUserRole(String userId) {
        return userRoleDAO.findByUserId(userId).stream().findFirst();
    }

    public Page<UserInformation> getAll(UserSearchParamRequest request, Pageable pageable) {
        Specification<User> spec = ignoreDeleted();
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("phoneNumber"), "%" + request.getPhone() + "%"));
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("email"), "%" + request.getEmail() + "%"));
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (root.get("firstName") != null) {
                    predicates.add(
                        cb.like(cb.lower(root.get("firstName")), "%" + request.getName().toLowerCase() + "%"));
                }
                if (root.get("lastName") != null) {
                    predicates.add(
                        cb.like(cb.lower(root.get("lastName")), "%" + request.getName().toLowerCase() + "%"));
                }
                if (root.get("middleName") != null) {
                    predicates.add(
                        cb.like(cb.lower(root.get("middleName")), "%" + request.getName().toLowerCase() + "%"));
                }
                return !predicates.isEmpty() ? cb.or(predicates.toArray(new Predicate[0])) : cb.conjunction();
            });
        }
        if (request.getRole() != null && !request.getRole().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role").get("id"), request.getRole()));
        }
        if (request.getRanking() != null && !request.getRanking().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ranking").get("id"), request.getRanking()));
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status").get("id"), request.getStatus()));
        }
        return repository.findAll(spec, pageable).map(userMapper::toDTO);
    }

    private Specification<User> ignoreDeleted() { // where deleted_yn <> true
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

}