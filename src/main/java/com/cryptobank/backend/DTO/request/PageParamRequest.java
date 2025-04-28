package com.cryptobank.backend.DTO.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@ParameterObject
public class PageParamRequest {

    @Parameter(description = "Số trang muốn hiển thị")
    @Schema(defaultValue = "1")
    @Min(1)
    private int page = 1;

    @Parameter(description = "Số lượng phần tử mỗi trang [1-100]")
    @Min(1)
    @Max(100)
    @Schema(defaultValue = "10")
    private int size = 10;

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size);
    }

}
