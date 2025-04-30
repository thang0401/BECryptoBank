package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.EmployeeDTO;
import com.cryptobank.backend.DTO.request.EmployeeCreateRequest;
import com.cryptobank.backend.DTO.request.EmployeeSearchParamRequest;
import com.cryptobank.backend.DTO.request.EmployeeUpdateRequest;
import com.cryptobank.backend.DTO.request.PageParamRequest;
import com.cryptobank.backend.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/employees", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Nhân viên")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy thông tin employee theo id"
    )
    public EmployeeDTO getEmployeeById(@PathVariable String id) {
        return employeeService.toDTOFromId(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy danh sách employee",
        description = "Trả về danh sách các employee được phân trang với tham số page và size, " +
            "có thể tìm kiếm cụ thể theo số điện thoại, email, ..."
    )
    public PagedModel<EmployeeDTO> getAllEmployees(
        EmployeeSearchParamRequest request,
        @Valid PageParamRequest pageRequest
    ) {
        Page<EmployeeDTO> employees = employeeService.getAll(request, pageRequest.toPageable());
        return new PagedModel<>(employees);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Tạo mới một employee"
    )
    public EmployeeDTO createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return employeeService.save(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Cập nhật thông tin employee theo id"
    )
    public EmployeeDTO updateEmployee(
        @PathVariable String id,
        @Valid @RequestBody EmployeeUpdateRequest request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Xóa một employee theo id"
    )
    public boolean deleteEmployee(@PathVariable String id) {
        return employeeService.delete(id);
    }

}