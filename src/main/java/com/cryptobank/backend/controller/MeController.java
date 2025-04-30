package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.exception.JwtEmptyException;
import com.cryptobank.backend.model.CustomerUserDetails;
import com.cryptobank.backend.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "Me", description = "Thông tin user đã đăng nhập")
@SecurityRequirement(name = "Bearer Authorization")
public class MeController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserInformation get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken usernameToken) {
            if (usernameToken.getPrincipal() instanceof CustomerUserDetails userDetails) {
                return userService.get(userDetails.getUsername());
            }
        }
        throw new JwtEmptyException("Token is empty");
    }

}
