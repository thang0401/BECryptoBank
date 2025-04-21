package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.UserCreateRequest;
import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.DTO.UserUpdateRequest;
import com.cryptobank.backend.DTO.request.PageParamRequest;
import com.cryptobank.backend.DTO.request.UserSearchParamRequest;
import com.cryptobank.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "User", description = "Người dùng")
public class UserController {

	private final UserService userService;

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "Lấy thông tin user theo id"
	)
	public UserInformation getUserById(@PathVariable String id) {
		return userService.get(id);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "Lấy danh sách user",
		description = "Trả về danh sách các user được phân trang với tham số page và size, " +
			"có thể tìm kiếm cụ thể theo số điện thoại, email, ..."
	)
	public PagedModel<UserInformation> getAllUsers(
		UserSearchParamRequest request,
		@Valid PageParamRequest pageRequest
	) {
		Page<UserInformation> users = userService.getAll(request, pageRequest.toPageable());
		return new PagedModel<>(users);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
		summary = "Tạo mới một user"
	)
	public UserInformation createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.save(request);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "Cập nhật thông tin user theo id"
	)
	public UserInformation updateUser(
			@PathVariable String id,
			@Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request, "system");
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "Xóa một user theo id"
	)
	public void deleteUser(@PathVariable String id) {
		userService.delete(id, null);
	}

}