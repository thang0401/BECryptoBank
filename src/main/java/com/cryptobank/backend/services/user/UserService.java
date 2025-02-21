package com.cryptobank.backend.services.user;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.repository.UserDAO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean checkUserAuthenticated(HttpSession session) {
        return true;
    }

    @Override
    public User getUserId(String id) {
        User customer = userDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer '" + id + "' not found"));
        return customer;
    }

    @Override
    public List<User> getUserName(String name) {
        List<User> customers = userDAO.findByName(name);
        if (customers.isEmpty())
            throw new ResourceNotFoundException("customer '" + name + "' not found");
        return customers;
    }

    @Override
    public User getUserEmail(String email) {
        User customer = userDAO.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("customer '" + email + "' not found"));
        return customer;
    }

    @Override
    public List<User> Users() {
        return userDAO.findAll();
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        return userDAO.findAll(PageRequest.of(page, size));
    }

    @Override
    public User createUser(User customer) {
    	userDAO.findByEmail(customer.getEmail())
            .ifPresentOrElse(
                c -> {
                    throw new AlreadyExistException("customer '" + customer.getEmail() + "' already exist");
                },
                () -> {
                    //TODO
                    // create address, role in db if not exist,
                    // create default portfolio for new customer
                    // before save
                    customer.setPassword(passwordEncoder.encode(customer.getPassword()));
                    userDAO.save(customer);
                }
            );
        return customer;
    }

    @Override
    public User updateUser(String id, User customer) {
        User currentCustomer = getUserId(id);
        User newCustomer = updateExistingCustomer(currentCustomer, customer);
        return userDAO.save(newCustomer);
    }

    @Override
    public void deleteUser(String id) {
    	userDAO.findById(id)
                .ifPresentOrElse(userDAO::delete, () -> {
                    throw new ResourceNotFoundException("customer '" + id + "' not found");
                });
    }

    private User updateExistingCustomer(User current, User newUser) {
        return current.builder()
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
//                .role_id(newCustomer.getRole_id())
                .phone(newUser.getPhone())
                .gender(newUser.getGender())
//                .email(newCustomer.getEmail())
                .avatarURL(newUser.getAvatarURL())
                .status(newUser.getStatus())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .smartOTP(newUser.getSmartOTP())
                .idNumber(newUser.getIdNumber())
                .googleId(newUser.getGoogleId())
//                .address_id(newCustomer.getAddress_id())
                .dateOfBirth(newUser.getDateOfBirth())
                .idCardFrontImgURL(newUser.getIdCardFrontImgURL())
                .idCardBackImgURL(newUser.getIdCardBackImgURL())
                .activated(newUser.getActivated())
                .deleted(newUser.getDeleted())
                .modifiedDate(newUser.getModifiedDate())
                .modifiedBy(newUser.getModifiedBy())
                .build();
    }

}
