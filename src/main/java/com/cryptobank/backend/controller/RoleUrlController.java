package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.RoleUrlDTO;
import com.cryptobank.backend.DTO.request.PageParamRequest;
import com.cryptobank.backend.DTO.request.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUrlUpdateRequest;
import com.cryptobank.backend.services.RoleUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/role/url", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Role Url", description = "Đường dẫn url cho từng vai trò tương ứng")
@SecurityRequirement(name = "Bearer Authorization")
public class RoleUrlController {

    private final RoleUrlService roleUrlService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy danh sách role url",
        description = "Trả về danh sách các role url được phân trang với tham số page và size, có thể tìm kiếm cụ thể theo role id"
    )
    public PagedModel<RoleUrlDTO> getAllRoleRoleUrls(
        @Parameter(description = "ID role") @RequestParam(required = false) String roleId,
        @Valid PageParamRequest request
    ) {
        return new PagedModel<>(roleUrlService.getAll(roleId, request.toPageable()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleUrlDTO getRoleRoleUrlById(@PathVariable String id) {
        return roleUrlService.toResponseFromId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RoleUrlDTO addRoleUrl(@Valid @RequestBody RoleUrlCreateRequest request) {
        return roleUrlService.save(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleUrlDTO updateRoleUrl(@PathVariable String id, @Valid @RequestBody RoleUrlUpdateRequest request) {
        return roleUrlService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteRoleUrl(@PathVariable String id) {
        return roleUrlService.delete(id);
    }

}
