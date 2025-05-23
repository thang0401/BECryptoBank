package com.cryptobank.backend.controller;

import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.services.SavingAccountService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class SavingAccountController {

    SavingAccountService userPortfolioService;

    @GetMapping("/customer-management")
    public ResponseEntity<List<SavingAccount>> getAll() {
        return ResponseEntity.ok(userPortfolioService.findAll());
    }

    @GetMapping("/customer-management/{id}")
    public ResponseEntity<SavingAccount> getOne(@PathVariable("id") String id) {
        if (!userPortfolioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userPortfolioService.findById(id));
    }

    @PostMapping("/customer-management")
    public ResponseEntity<SavingAccount> post(@RequestBody SavingAccount userPortfolio) {
        if (userPortfolioService.existsById(userPortfolio.getId())) {
            return ResponseEntity.badRequest().build();
        }
        userPortfolioService.save(userPortfolio);
        return ResponseEntity.ok(userPortfolio);
    }

    @PutMapping("/customer-management/{id}")
    public ResponseEntity<SavingAccount> put(@PathVariable("id") String id, @RequestBody SavingAccount userPortfolio) {
        if (!userPortfolioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userPortfolioService.save(userPortfolio);
        return ResponseEntity.ok(userPortfolio);
    }

    @DeleteMapping("/customer-management/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (!userPortfolioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userPortfolioService.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // Tìm theo customer_id
    @GetMapping("/customer-management/customer-id/{customerId}")
    public ResponseEntity<List<SavingAccount>> getUserPortfoliosByCustomerId(@PathVariable("customerId") String customerId) {
        List<SavingAccount> userPortfolios = userPortfolioService.getUserPortfoliosByCustomerId(customerId);
        return ResponseEntity.ok(userPortfolios);
    }

    // Tìm theo vai trò của User
//    @GetMapping("/customer-management/role/{roleName}")
//    public ResponseEntity<List<SavingAccount>> getUserPortfoliosByRoleName(@PathVariable("roleName") String roleName) {
//        List<SavingAccount> userPortfolios = userPortfolioService.getUserPortfoliosByRoleName(roleName);
//        return ResponseEntity.ok(userPortfolios);
//    }

    // Tìm theo ranking ID
//    @GetMapping("/customer-management/ranking-id/{rankingId}")
//    public ResponseEntity<List<SavingAccount>> getUserPortfoliosByRankingId(@PathVariable("rankingId") String rankingId) {
//        List<SavingAccount> userPortfolios = userPortfolioService.getUserPortfoliosByRankingId(rankingId);
//        return ResponseEntity.ok(userPortfolios);
//    }

    // Tìm theo số điện thoại
    @GetMapping("/customer-management/phone/{phoneNum}")
    public ResponseEntity<List<SavingAccount>> getUserPortfoliosByPhoneNumber(@PathVariable("phoneNum") String phoneNum) {
        List<SavingAccount> userPortfolios = userPortfolioService.getUserPortfoliosByPhoneNumber(phoneNum);
        return ResponseEntity.ok(userPortfolios);
    }

    // Tìm theo tên User (first name hoặc last name)
    @GetMapping("/customer-management/name/{name}")
    public ResponseEntity<List<SavingAccount>> getUserPortfoliosByUserName(@PathVariable("name") String name) {
        List<SavingAccount> userPortfolios = userPortfolioService.getUserPortfoliosByUserName(name);
        return ResponseEntity.ok(userPortfolios);
    }

    // Tìm theo id_card của Heir
//    @GetMapping("/customer-management/id-card/{idCard}")
//    public ResponseEntity<List<SavingAccount>> getUserPortfoliosByIdCard(@PathVariable("idCard") String idCard) {
//        List<SavingAccount> userPortfolios = userPortfolioService.getUserPortfoliosByIdCard(idCard);
//        return ResponseEntity.ok(userPortfolios);
//    }
}