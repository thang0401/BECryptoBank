package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.services.UserService;
import com.cryptobank.backend.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
@Tag(name = "JWT", description = "Tạo jwt giả để cho việc testing")
public class JwtController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String jwtGenerate(
        @Parameter(description = "ID của user") @PathVariable String id,
        @Parameter(description = "Thời lượng của một token") @RequestParam long expiration
    ) {
        UserInformation user = userService.get(id);
        return jwtUtil.generateToken(user, expiration);
    }
}
