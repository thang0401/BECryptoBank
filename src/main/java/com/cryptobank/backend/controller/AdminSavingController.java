package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.AdminSavingAccountDTO;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.repository.SavingAccountDAO;
import java.util.List;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/saving")
@AllArgsConstructor
public class AdminSavingController {

    @Autowired
    SavingAccountDAO savingAccountDAO;

    @GetMapping("/get-saving-list")
    public ResponseEntity<List<AdminSavingAccountDTO>> getSavingList() {
        return ResponseEntity.ok(savingAccountDAO.findAllDTO());
    }
    
    @GetMapping("/get-saving-list/{id}")
    public ResponseEntity<SavingAccount> getSpecificSaving(@PathVariable String id) {
        return ResponseEntity.ok(savingAccountDAO.findById(id).orElse(null));
    }
    

}
