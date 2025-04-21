package com.cryptobank.backend.DTO.request;

import io.swagger.v3.oas.annotations.Parameter;
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
    @Min(1)
    private int page = 1;

    @Parameter(description = "Số lượng phần tử mỗi trang [1-100]")
    @Min(1)
    @Max(100)
    private int size = 10;

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size);
    }

}
