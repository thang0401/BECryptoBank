package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.RoleUrlDTO;
import com.cryptobank.backend.DTO.request.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUrlUpdateRequest;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.services.RoleUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/role/url")
public class RoleUrlController {

    private final RoleUrlService roleUrlService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<RoleUrlDTO>>> getAllRoleRoleUrls(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse<>("", new PagedModel<>(roleUrlService.getAll(PageRequest.of(page - 1, size)))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleUrlDTO>> getRoleRoleUrlById(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.toResponseFromId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleUrlDTO>> addRoleUrl(@Valid @RequestBody RoleUrlCreateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.save(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleUrlDTO>> updateRoleUrl(@PathVariable String id, @Valid @RequestBody RoleUrlUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteRoleUrl(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.delete(id)));
    }

}
