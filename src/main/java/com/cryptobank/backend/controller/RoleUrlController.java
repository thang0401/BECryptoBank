package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.RoleUrlDTO;
import com.cryptobank.backend.DTO.request.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUrlUpdateRequest;
import com.cryptobank.backend.services.RoleUrlService;
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
@RequestMapping("/api/role/url")
public class RoleUrlController {

    private final RoleUrlService roleUrlService;

    @GetMapping
    public ResponseEntity<PagedModel<RoleUrlDTO>> getAllRoleRoleUrls(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new PagedModel<>(roleUrlService.getAll(PageRequest.of(page - 1, size))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleUrlDTO> getRoleRoleUrlById(@PathVariable String id) {
        return ResponseEntity.ok(roleUrlService.toResponseFromId(id));
    }

    @PostMapping
    public ResponseEntity<RoleUrlDTO> addRoleUrl(@Valid @RequestBody RoleUrlCreateRequest request) {
        return ResponseEntity.ok(roleUrlService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleUrlDTO> updateRoleUrl(@PathVariable String id, @Valid @RequestBody RoleUrlUpdateRequest request) {
        return ResponseEntity.ok(roleUrlService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteRoleUrl(@PathVariable String id) {
        return ResponseEntity.ok(roleUrlService.delete(id));
    }

}
