package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.StatusDTO;
import com.cryptobank.backend.DTO.request.PageParamRequest;
import com.cryptobank.backend.services.StatusService;
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

@RestController
@RequestMapping(value = "/api/status", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Status", description = "Trạng thái")
public class StatusController {

    private final StatusService statusService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy danh sách status",
        description = "Trả về danh sách các status được phân trang với tham số page và size, có thể tìm status thuộc một group nhất định"
    )
    public PagedModel<StatusDTO> getAllStatuses(
        @Parameter(description = "ID group") @RequestParam(required = false) String groupId,
        @Valid @ParameterObject PageParamRequest request
    ) {
        return new PagedModel<>(statusService.getAll(groupId, request.toPageable()));
    }

}
