package com.cryptobank.backend.services;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InitializeWalletService {

    @Autowired
    private UserDAO userDAO;

    @Transactional
    public User InitializeWallet(String id, String walletAddress) {
        // Tìm user theo id
        User user = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found or has been deleted."));

        // Cập nhật wallet_address
        user.setWalletAddress(walletAddress);
        return userDAO.save(user); // Lưu lại thay đổi
    }
}