package com.cryptobank.backend.services.user;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.services.generalServices.EmailService;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private EmailService emailService;

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
                .activated(newUser.isActivated())
                .deleted(newUser.isDeleted())
                .modifiedDate(newUser.getModifiedDate())
                .modifiedBy(newUser.getModifiedBy())
                .build();
    }

    
    //Use for register
	@Override
	public Boolean existsByEmail(String email) {
		if(userDAO.findByEmail(email).isEmpty())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public Boolean existsByPhoneNumber(String phoneNumber) {
		if(userDAO.findByPhone(phoneNumber).isEmpty())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public Boolean existsByIdCardNumber(String cardNumber) {
		if(userDAO.findByIdNumber(cardNumber).isEmpty())
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	// use for email
	public void requestResetPassword(String email, HttpSession session) {
        Optional<User> user = userDAO.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("Email không tồn tại");
        }

        // Tạo mã xác thực ngẫu nhiên 6 chữ số
        String resetCode = generateResetCode();
        session.setAttribute("OTP", resetCode);
        session.setAttribute("time", LocalDateTime.now().plusMinutes(15));// Mã hết hạn sau 15 phút

        // Gửi email với mã xác thực
        emailService.sendResetPasswordEmail(email, resetCode);
    }
	
	 private String generateResetCode() {
	        int randomCode = (int) (Math.random() * 900000) + 100000;  // Tạo mã 6 chữ số
	        return String.valueOf(randomCode);
	    }
	 
	 
	 // Phương thức xử lý logic thay đổi mật khẩu
	    public void resetPassword(String email, String resetCode, String newPassword, HttpSession session) {
	        // Tìm người dùng qua email
	        Optional<User> user = userDAO.findByEmail(email);
	        if (user.isEmpty()) {
	            throw new RuntimeException("Email không tồn tại");
	        }

	        // Kiểm tra mã resetCode có đúng không
	        String OTP=(String) session.getAttribute("OTP");
	        if (!OTP.equals(resetCode)) {
	            throw new RuntimeException("Mã xác thực không hợp lệ");
	        }

	        // Kiểm tra mã resetCode có hết hạn hay không
	        LocalDateTime timeLimit=(LocalDateTime)session.getAttribute("time");
	        if (!timeLimit.isBefore(LocalDateTime.now())) {
	            throw new RuntimeException("Mã xác thực đã hết hạn");
	        }

	        // Mã xác thực hợp lệ, tiến hành thay đổi mật khẩu
	        // Mã hóa mật khẩu mới
	        String encodedPassword = new BCryptPasswordEncoder().encode(newPassword);
	        User userChangePass=userDAO.getByEmail(email);
	        userChangePass.setPassword(encodedPassword);
	        
	        // Lưu thông tin người dùng mới với mật khẩu đã thay đổi vào database
	        userDAO.save(userChangePass);
	    }

}
