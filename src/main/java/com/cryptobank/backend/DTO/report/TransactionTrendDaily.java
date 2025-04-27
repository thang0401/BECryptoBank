package com.cryptobank.backend.DTO.report;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionTrendDaily {
	private Integer month;
    private Long totalAmount;
    private BigDecimal growthPercentage;
    private List<Long> trendData;
}
