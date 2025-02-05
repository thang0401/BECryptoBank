package com.cryptobank.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cryptobank.backend.services.generalServices.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AuthController {
	@Autowired
    private AuthService authService;
	
	// Đăng nhập
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpServletRequest request, HttpSession session) {
        // Gọi phương thức login từ AuthService để xử lý logic đăng nhập
        return authService.login(email, password, request, session);
    }

    // Đăng xuất
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // Gọi phương thức logout từ AuthService để xử lý logic đăng xuất
        authService.logout(session);
        return "Đăng xuất thành công!";
    }
}
