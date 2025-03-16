package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.RoleDTO;
import com.cryptobank.backend.DTO.request.RoleCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUpdateRequest;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.services.generalServices.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<RoleDTO>>> getAllRoles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse<>("", new PagedModel<>(roleService.getAll(PageRequest.of(page - 1, size)))));
    }

    @GetMapping("/{id}/count")
    public ResponseEntity<ApiResponse<Integer>> getRoleCount(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("", roleService.count(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleById(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("", roleService.toResponseFromId(id)));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleByName(@PathVariable String name) {
        return ResponseEntity.ok(new ApiResponse<>("", roleService.toResponseFromName(name)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleDTO>> addRole(@Valid @RequestBody RoleCreateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("", roleService.save(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(@PathVariable String id, @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("", roleService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteRole(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("", roleService.deleteById(id)));
    }

}
