package com.cryptobank.backend.controller;

import org.springframework.web.bind.annotation.*;

import com.cryptobank.backend.DTO.SavingAccountDTO;
import com.cryptobank.backend.entity.DebitAccount;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.entity.Term;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.SavingAccountDAO;
import com.cryptobank.backend.repository.TermDAO;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/saving")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Thêm dòng này
public class SavingController {

    TermDAO termDAO;
    SavingAccountDAO savingAccountDAO;


    @GetMapping("/add-saving-asset/open")
    public String getData(@RequestParam String param) {
        List<Term> terms=getTerm();
        
        return new String();
    }

    @GetMapping("/get-saving-list")
    public ResponseEntity<List<SavingAccountDTO>> getSavingList() {
        return ResponseEntity.ok(savingAccountDAO.findAllDTO());
    }
    
    @GetMapping("/get-saving-list/{id}")
    public ResponseEntity<SavingAccount> getSpecificSaving(@RequestParam String id) {
        return ResponseEntity.ok(savingAccountDAO.findById(id).orElse(null));
    }
    
    private List<Term> getTerm(){
        return termDAO.findAll();
    }

    private User getUserAccount(){  
        return null;
    }

    private List<DebitAccount> getUserDebitAccounts(){
        User user=getUserAccount();
        return user.getDebitAccounts();
    }


}
