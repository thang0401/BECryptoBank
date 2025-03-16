package com.cryptobank.backend.controller;

import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.utils.IdGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/id")
public class IdGeneratorController {

    @GetMapping
    public ResponseEntity<ApiResponse<String>> getId() {
        return ResponseEntity.ok(new ApiResponse<>("", IdGenerator.generate()));
    }

}
