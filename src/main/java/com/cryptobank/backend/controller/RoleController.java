package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.RoleDTO;
import com.cryptobank.backend.DTO.request.RequestPageParam;
import com.cryptobank.backend.DTO.request.RoleCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUpdateRequest;
import com.cryptobank.backend.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/role", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Role", description = "Vai trò")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy danh sách role",
        description = "Trả về danh sách các role được phân trang với tham số page và size, có thể tìm kiếm cụ thể theo status id"
    )
    public PagedModel<RoleDTO> getAllRoles(
        @Parameter(description = "ID status") @RequestParam(required = false) String statusId,
        @Valid @ParameterObject RequestPageParam request
    ) {
        return new PagedModel<>(roleService.getAll(statusId, request.toPageable()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy thông tin role theo id"
    )
    public RoleDTO getRoleById(
        @Parameter(description = "ID role") @PathVariable String id
    ) {
        return roleService.toDTOFromId(id);
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy thông tin role theo tên"
    )
    public RoleDTO getRoleByName(
        @Parameter(description = "Tên role") @PathVariable String name
    ) {
        return roleService.toResponseFromName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Tạo mới một role"
    )
    public RoleDTO addRole(@Valid @RequestBody RoleCreateRequest request) {
        return roleService.save(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Cập nhật thông tin role theo id"
    )
    public RoleDTO updateRole(
        @Parameter(description = "ID role") @PathVariable String id,
        @Valid @RequestBody RoleUpdateRequest request
    ) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Xóa một role theo id"
    )
    public Boolean deleteRole(
        @Parameter(description = "ID role") @PathVariable String id
    ) {
        return roleService.deleteById(id);
    }

}
