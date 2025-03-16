package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.RoleUrlUpdateRequest;
import com.cryptobank.backend.entity.RoleUrl;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.services.generalServices.RoleUrlService;
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
    public ResponseEntity<ApiResponse<PagedModel<RoleUrl>>> getAllRoleRoleUrls(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse<>("", new PagedModel<>(roleUrlService.getAll(PageRequest.of(page, size)))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleUrl>> getRoleRoleUrlById(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.getById(id)));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<RoleUrl>> getRoleByUrl(@PathVariable String name) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.getByUrl(name)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleUrl>> addRoleUrl(@Valid @RequestBody RoleUrlCreateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.save(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleUrl>> updateRoleUrl(@PathVariable String id, @Valid @RequestBody RoleUrlUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteRoleUrl(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("", roleUrlService.delete(id)));
    }

}
