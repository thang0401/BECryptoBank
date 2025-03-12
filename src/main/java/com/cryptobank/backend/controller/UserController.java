package com.cryptobank.backend.controller;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.services.generalServices.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("", userService.get(id)));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<List<User>>> getUserByName(@PathVariable String name) {
        return ResponseEntity.ok(new ApiResponse<>("", userService.getName(name)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(new ApiResponse<>("", userService.getEmail(email)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse<>("", userService.getAll()));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse<>("", userService.getAll(page, size)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> creatUser(@RequestBody User user) {
        return ResponseEntity.ok(new ApiResponse<>("", userService.save(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable String id, @RequestBody User user) {
        userService.update(id, user);
        return ResponseEntity.ok(new ApiResponse<>("", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    	userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Tìm theo vai trò của User
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<User>> getUsersByRoleName(@PathVariable("roleName") String roleName) {
        List<User> Users = userService.getUsersByRoleName(roleName);
        return ResponseEntity.ok(Users);
    }

    // Tìm theo ranking ID
    @GetMapping("/ranking/{rankingName}")
    public ResponseEntity<List<User>> getUsersByRankingName(@PathVariable("rankingName") String rankingName) {
        List<User> Users = userService.getUsersByRankingName(rankingName);
        return ResponseEntity.ok(Users);
    }

    // Tìm theo số điện thoại
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<List<User>> getUsersByPhoneNumber(@PathVariable("phoneNumber") String phoneNum) {
        List<User> Users = userService.getUsersByPhoneNumber(phoneNum);
        return ResponseEntity.ok(Users);
    }

    // Tìm theo id_number của Heir
    @GetMapping("/id-card/{idNumber}")
    public ResponseEntity<User> getUsersByIdNumber(@PathVariable("idNumber") String idNumber) {
        return ResponseEntity.ok(userService.getUsersByIdNumber(idNumber));
    }

}
