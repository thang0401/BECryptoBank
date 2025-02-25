// package com.cryptobank.backend.controller;

// import java.util.List;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;

// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;

// import org.springframework.web.bind.annotation.RestController;

// import com.cryptobank.backend.entity.UserPortfolio;
// import com.cryptobank.backend.services.generalServices.UserPortfolioService;




// @CrossOrigin("*")
// @RestController
// public class CustomerPortfolioController {

// 	 @Autowired
// 	UserPortfolioService userPortfolioService;

//     @GetMapping("/customer-management")
//     public ResponseEntity<List<UserPortfolio>> getAll() {
//         return ResponseEntity.ok(userPortfolioService.findAll());
//     }

//     @GetMapping("/customer-management/{id}")
//     public ResponseEntity<UserPortfolio> getOne(@PathVariable("id") String id) {
//         if (!userPortfolioService.existsById(id)) {
//             return ResponseEntity.notFound().build();
//         }
//         return ResponseEntity.ok(userPortfolioService.findById(id));
//     }

//     @PostMapping("/customer-management")
//     public ResponseEntity<UserPortfolio> post(@RequestBody UserPortfolio userPortfolio) {
//         if (userPortfolioService.existsById(userPortfolio.getId())) {
//             return ResponseEntity.badRequest().build();
//         }
//         userPortfolioService.save(userPortfolio);
//         return ResponseEntity.ok(userPortfolio);
//     }

//     @PutMapping("/customer-management/{id}")
//     public ResponseEntity<UserPortfolio> put(@PathVariable("id") String id, @RequestBody UserPortfolio userPortfolio) {
//         if (!userPortfolioService.existsById(id)) {
//             return ResponseEntity.notFound().build();
//         }
//         userPortfolioService.save(userPortfolio);
//         return ResponseEntity.ok(userPortfolio);
//     }

//     @DeleteMapping("/customer-management/{id}")
//     public ResponseEntity<Void> delete(@PathVariable("id") String id) {
//         if (!userPortfolioService.existsById(id)) {
//             return ResponseEntity.notFound().build();
//         }
//         userPortfolioService.deleteById(id);
//         return ResponseEntity.ok().build();
//     }
    
//     // Tìm theo customer_id
//     @GetMapping("/customer-management/customer-id/{customerId}")
//     public ResponseEntity<List<UserPortfolio>> getUserPortfoliosByCustomerId(@PathVariable("customerId") String customerId) {
//         List<UserPortfolio> userPortfolios = userPortfolioService.getUserPortfoliosByCustomerId(customerId);
//         return ResponseEntity.ok(userPortfolios);
//     }

//     // Tìm theo vai trò của User
//     @GetMapping("/customer-management/role/{roleName}")
//     public ResponseEntity<List<UserPortfolio>> getUserPortfoliosByRoleName(@PathVariable("roleName") String roleName) {
//         List<UserPortfolio> userPortfolios = userPortfolioService.getUserPortfoliosByRoleName(roleName);
//         return ResponseEntity.ok(userPortfolios);
//     }

//     // Tìm theo ranking ID
//     @GetMapping("/customer-management/ranking-id/{rankingId}")
//     public ResponseEntity<List<UserPortfolio>> getUserPortfoliosByRankingId(@PathVariable("rankingId") String rankingId) {
//         List<UserPortfolio> userPortfolios = userPortfolioService.getUserPortfoliosByRankingId(rankingId);
//         return ResponseEntity.ok(userPortfolios);
//     }

//     // Tìm theo số điện thoại
//     @GetMapping("/customer-management/phone/{phoneNum}")
//     public ResponseEntity<List<UserPortfolio>> getUserPortfoliosByPhoneNumber(@PathVariable("phoneNum") String phoneNum) {
//         List<UserPortfolio> userPortfolios = userPortfolioService.getUserPortfoliosByPhoneNumber(phoneNum);
//         return ResponseEntity.ok(userPortfolios);
//     }

//     // Tìm theo tên User (first name hoặc last name)
//     @GetMapping("/customer-management/name/{name}")
//     public ResponseEntity<List<UserPortfolio>> getUserPortfoliosByUserName(@PathVariable("name") String name) {
//         List<UserPortfolio> userPortfolios = userPortfolioService.getUserPortfoliosByUserName(name);
//         return ResponseEntity.ok(userPortfolios);
//     }

//     // Tìm theo id_card của Heir
//     @GetMapping("/customer-management/id-card/{idCard}")
//     public ResponseEntity<List<UserPortfolio>> getUserPortfoliosByIdCard(@PathVariable("idCard") String idCard) {
//         List<UserPortfolio> userPortfolios = userPortfolioService.getUserPortfoliosByIdCard(idCard);
//         return ResponseEntity.ok(userPortfolios);
//     }
// }