package com.example.BE_Crypto_Bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import com.example.BE_Crypto_Bank.entity.User_portfolio;
import com.example.BE_Crypto_Bank.service.UserPortfolioService;




@CrossOrigin("*")
@RestController
public class CustomerPortfolioController {

	 @Autowired
	UserPortfolioService userPortfolioService;

    @GetMapping("/customer-management")
    public ResponseEntity<List<User_portfolio>> getAll() {
        return ResponseEntity.ok(userPortfolioService.findAll());
    }

    @GetMapping("/customer-management/{id}")
    public ResponseEntity<User_portfolio> getOne(@PathVariable("id") String id) {
        if (!userPortfolioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userPortfolioService.findById(id));
    }

    @PostMapping("/customer-management")
    public ResponseEntity<User_portfolio> post(@RequestBody User_portfolio userPortfolio) {
        if (userPortfolioService.existsById(userPortfolio.getId())) {
            return ResponseEntity.badRequest().build();
        }
        userPortfolioService.save(userPortfolio);
        return ResponseEntity.ok(userPortfolio);
    }

    @PutMapping("/customer-management/{id}")
    public ResponseEntity<User_portfolio> put(@PathVariable("id") String id, @RequestBody User_portfolio userPortfolio) {
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
    public ResponseEntity<List<User_portfolio>> getUserPortfoliosByCustomerId(@PathVariable("customerId") String customerId) {
        List<User_portfolio> userPortfolios = userPortfolioService.getUserPortfoliosByCustomerId(customerId);
        return ResponseEntity.ok(userPortfolios);
    }

    // Tìm theo vai trò của User
    @GetMapping("/customer-management/role/{roleName}")
    public ResponseEntity<List<User_portfolio>> getUserPortfoliosByRoleName(@PathVariable("roleName") String roleName) {
        List<User_portfolio> userPortfolios = userPortfolioService.getUserPortfoliosByRoleName(roleName);
        return ResponseEntity.ok(userPortfolios);
    }

    // Tìm theo ranking ID
    @GetMapping("/customer-management/ranking-id/{rankingId}")
    public ResponseEntity<List<User_portfolio>> getUserPortfoliosByRankingId(@PathVariable("rankingId") String rankingId) {
        List<User_portfolio> userPortfolios = userPortfolioService.getUserPortfoliosByRankingId(rankingId);
        return ResponseEntity.ok(userPortfolios);
    }

    // Tìm theo số điện thoại
    @GetMapping("/customer-management/phone/{phoneNum}")
    public ResponseEntity<List<User_portfolio>> getUserPortfoliosByPhoneNumber(@PathVariable("phoneNum") String phoneNum) {
        List<User_portfolio> userPortfolios = userPortfolioService.getUserPortfoliosByPhoneNumber(phoneNum);
        return ResponseEntity.ok(userPortfolios);
    }

    // Tìm theo tên User (first name hoặc last name)
    @GetMapping("/customer-management/name/{name}")
    public ResponseEntity<List<User_portfolio>> getUserPortfoliosByUserName(@PathVariable("name") String name) {
        List<User_portfolio> userPortfolios = userPortfolioService.getUserPortfoliosByUserName(name);
        return ResponseEntity.ok(userPortfolios);
    }

    // Tìm theo id_card của Heir
    @GetMapping("/customer-management/id-card/{idCard}")
    public ResponseEntity<List<User_portfolio>> getUserPortfoliosByIdCard(@PathVariable("idCard") String idCard) {
        List<User_portfolio> userPortfolios = userPortfolioService.getUserPortfoliosByIdCard(idCard);
        return ResponseEntity.ok(userPortfolios);
    }
}