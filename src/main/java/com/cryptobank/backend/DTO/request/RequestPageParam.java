package com.cryptobank.backend.DTO.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RequestPageParam {

    @Parameter(description = "Số trang muốn hiển thị", example = "1")
    @Min(1)
    private int page = 1;
    @Parameter(description = "Số lượng phần tử mỗi trang", example = "10")
    @Min(1)
    private int size = 10;

}
