package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.UserCreateRequest;
import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.DTO.UserUpdateRequest;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.services.generalServices.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;




@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<UserInformation>> getUserById(@PathVariable String id) {
		UserInformation user = userService.get(id);
		return ResponseEntity.ok(new ApiResponse<>("User retrieved successfully", user));
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<ApiResponse<UserInformation>> getUserByEmail(@PathVariable String email) {
		UserInformation user = userService.getEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("User with email " + email + " not found", null));
		}
		return ResponseEntity.ok(new ApiResponse<>("User retrieved successfully", user));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserInformation>>> getAllUsers() {
		List<UserInformation> users = userService.getAll();
		return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", users));
	}

	@GetMapping("/page")
	public ResponseEntity<ApiResponse<Page<UserInformation>>> getAllUsers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<UserInformation> users = userService.getAll(page, size);
		return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", users));
	}

	@PostMapping
    public ResponseEntity<ApiResponse<UserInformation>> createUser(
            @Valid @RequestBody UserCreateRequest request, 
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Validation failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(errorMessage, null));
        }

        UserInformation createdUser = userService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User created successfully", createdUser));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserInformation>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request, 
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Validation failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(errorMessage, null));
        }

        UserInformation updatedUser = userService.update(id, request, "system");
        return ResponseEntity.ok(new ApiResponse<>("User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable String id) {
		try {
			userService.delete(id, null);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@GetMapping("/role/{roleName}")
	public ResponseEntity<ApiResponse<List<UserInformation>>> getUsersByRoleName(
			@PathVariable("roleName") String roleName) {
		List<UserInformation> users = userService.getUsersByRoleName(roleName);
		return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", users));
	}

	@GetMapping("/ranking/{rankingName}")
	public ResponseEntity<ApiResponse<List<UserInformation>>> getUsersByRankingName(
			@PathVariable("rankingName") String rankingName) {
		List<UserInformation> users = userService.getUsersByRankingName(rankingName);
		return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", users));
	}

	@GetMapping("/phone/{phoneNumber}")
	public ResponseEntity<ApiResponse<List<UserInformation>>> getUsersByPhoneNumber(
			@PathVariable("phoneNumber") String phoneNum) {
		List<UserInformation> users = userService.getUsersByPhoneNumber(phoneNum);
		return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", users));
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<ApiResponse<List<UserInformation>>> getUsersByName(@PathVariable("name") String name) {
		List<UserInformation> users = userService.getUserByUserName(name);
		return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", users));
	}

	@GetMapping("/id_number/{idNumber}")
	public ResponseEntity<ApiResponse<UserInformation>> getUsersByIdNumber(@PathVariable("idNumber") String idNumber) {
		UserInformation user = userService.getUsersByIdNumber(idNumber);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("User not found", null));
		}
		return ResponseEntity.ok(new ApiResponse<>("User retrieved successfully", user));
	}
}

