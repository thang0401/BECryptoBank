package com.cryptobank.backend.controller;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserId(id));
    }

//    @GetMapping("/name/{name}")
//    public ResponseEntity<ApiResponse<List<User>>> getUserByName(@PathVariable String name) {
//        return ResponseEntity.ok(new ApiResponse<>("", userService.getName(name)));
//    }


    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.Users());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @PostMapping
    public ResponseEntity<User> creatUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable String id, 
            @RequestBody User user,
            @RequestParam String modifiedBy) { // Thêm người cập nhật
        userService.update(id, user, modifiedBy);
        return ResponseEntity.ok(new ApiResponse<>("", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            @RequestParam String deletedBy) { // Thêm người xóa
        userService.delete(id, deletedBy);
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
    
 // Tìm theo số điện thoại
    @GetMapping("/name/{name}")
    public ResponseEntity<List<User>> getUsersByName(@PathVariable("name") String name) {
        List<User> Users = userService.getUserByUserName(name);
        return ResponseEntity.ok(Users);
    }

    // Tìm theo id_card number
    @GetMapping("/id_number/{idNumber}")
    public ResponseEntity<User> getUsersByIdNumber(@PathVariable("idNumber") String idNumber) {
        return ResponseEntity.ok(userService.getUsersByIdNumber(idNumber));
    }


}
