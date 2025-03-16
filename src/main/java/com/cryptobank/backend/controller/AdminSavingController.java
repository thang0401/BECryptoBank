package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.AdminSavingAccountDTO;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.repository.SavingAccountDAO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/saving")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Thêm dòng này
public class AdminSavingController {

    SavingAccountDAO savingAccountDAO;

    @GetMapping("/get-saving-list")
    public ResponseEntity<ApiResponse<List<AdminSavingAccountDTO>>> getSavingList() {
        ApiResponse<List<AdminSavingAccountDTO>> apiResponse = new ApiResponse<>("", savingAccountDAO.findAllDTO());
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/get-saving-list/{id}")
    public ResponseEntity<ApiResponse<SavingAccount>> getSpecificSaving(@PathVariable String id) {
        SavingAccount savingAccount = savingAccountDAO.findById(id).orElse(null);
        if (savingAccount == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ApiResponse<>("", savingAccount));
    }
    
    


}
