package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.UserCreateRequest;
import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.DTO.UserUpdateRequest;
import com.cryptobank.backend.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

	private final UserService userService;

	@GetMapping("/{id}")
	public ResponseEntity<UserInformation> getUserById(@PathVariable String id) {
		UserInformation user = userService.get(id);
		return ResponseEntity.ok(user);
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
		UserInformation user = userService.getEmail(email);
		return ResponseEntity.ok(user);
	}

	@GetMapping
	public ResponseEntity<List<UserInformation>> getAllUsers() {
		List<UserInformation> users = userService.getAll();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/page")
	public ResponseEntity<PagedModel<UserInformation>> getAllUsers(@RequestParam(defaultValue = "0") int page,
																   @RequestParam(defaultValue = "10") int size) {
		Page<UserInformation> users = userService.getAll(page, size);
		return ResponseEntity.ok(new PagedModel<>(users));
	}

	@PostMapping
	public ResponseEntity<?> createUser(
			@Valid @RequestBody UserCreateRequest request) {
		UserInformation createdUser = userService.save(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserInformation> updateUser(
			@PathVariable String id,
			@Valid @RequestBody UserUpdateRequest request) {
		UserInformation updatedUser = userService.update(id, request, "system");
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable String id) {
		userService.delete(id, null);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/role/{roleName}")
	public ResponseEntity<List<UserInformation>> getUsersByRoleName(
			@PathVariable("roleName") String roleName) {
		List<UserInformation> users = userService.getUsersByRoleName(roleName);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/ranking/{rankingName}")
	public ResponseEntity<List<UserInformation>> getUsersByRankingName(
			@PathVariable("rankingName") String rankingName) {
		List<UserInformation> users = userService.getUsersByRankingName(rankingName);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/phone/{phoneNumber}")
	public ResponseEntity<List<UserInformation>> getUsersByPhoneNumber(
			@PathVariable("phoneNumber") String phoneNum) {
		List<UserInformation> users = userService.getUsersByPhoneNumber(phoneNum);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<List<UserInformation>> getUsersByName(@PathVariable("name") String name) {
		List<UserInformation> users = userService.getName(name);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/id_number/{idNumber}")
	public ResponseEntity<?> getUsersByIdNumber(@PathVariable("idNumber") String idNumber) {
		UserInformation user = userService.getUsersByIdNumber(idNumber);
		return ResponseEntity.ok(user);
	}
}