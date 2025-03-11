package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.AdminSavingAccountDTO;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.repository.SavingAccountDAO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cryptobank.backend.DTO.AdminSavingAccountDTO;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.entity.Term;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.SavingAccountDAO;
import com.cryptobank.backend.repository.TermDAO;

import lombok.AllArgsConstructor;

import java.util.List;


@RestController
@RequestMapping("/saving")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Thêm dòng này
public class AdminSavingController {

    SavingAccountDAO savingAccountDAO;

    @GetMapping("/get-saving-list")
    public ResponseEntity<List<AdminSavingAccountDTO>> getSavingList() {
        return ResponseEntity.ok(savingAccountDAO.findAllDTO());
    }
    
    @GetMapping("/get-saving-list/{id}")
    public ResponseEntity<SavingAccount> getSpecificSaving(@RequestParam String id) {
        return ResponseEntity.ok(savingAccountDAO.findById(id).orElse(null));
    }
    

}
