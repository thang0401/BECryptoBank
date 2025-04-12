package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.RoleDTO;
import com.cryptobank.backend.DTO.request.RoleCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUpdateRequest;
import com.cryptobank.backend.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<PagedModel<RoleDTO>> getAllRoles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new PagedModel<>(roleService.getAll(PageRequest.of(page - 1, size))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable String id) {
        return ResponseEntity.ok(roleService.toResponseFromId(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.toResponseFromName(name));
    }

    @PostMapping
    public ResponseEntity<RoleDTO> addRole(@Valid @RequestBody RoleCreateRequest request) {
        return ResponseEntity.ok(roleService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable String id, @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteRole(@PathVariable String id) {
        return ResponseEntity.ok(roleService.deleteById(id));
    }

}
