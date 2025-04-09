package com.cryptobank.backend.controller;


import com.cryptobank.backend.DTO.InitializeWallet;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.services.InitializeWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class InitializeWalletController {

    @Autowired
    private InitializeWalletService initializeWalletService;

    @PostMapping("/initialize-wallet")
    public ResponseEntity<User> updateWalletAddress(@RequestBody InitializeWallet request) {
        try {
            User updatedUser = initializeWalletService.InitializeWallet(request.getId(), request.getWalletAddress());
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
