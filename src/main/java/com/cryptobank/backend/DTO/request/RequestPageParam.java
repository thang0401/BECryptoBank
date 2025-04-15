package com.cryptobank.backend.DTO.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
public class RequestPageParam {

    @Parameter(description = "Số trang muốn hiển thị")
    @Min(1)
    private int page = 1;
    @Parameter(description = "Số lượng phần tử mỗi trang")
    @Min(1)
    private int size = 10;

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size);
    }

}
