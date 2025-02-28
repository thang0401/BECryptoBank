package com.cryptobank.backend.services.generalServices;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.UserDAO;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userRepository;

    public UserService(UserDAO userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy số dư USDC của user
    public Double getUserBalance(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getUsdcBalance).orElse(0.0);
    }

    // Giảm số dư USDC sau khi rút tiền thành công
    public void decreaseUserBalance(String userId, Double amount) {
        Optional<User> userOpt = userRepository.findById(userId);
        userOpt.ifPresent(user -> {
            user.setUsdcBalance(user.getUsdcBalance() - amount);
            userRepository.save(user);
        });
    }
}
