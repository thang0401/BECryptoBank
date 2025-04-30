package com.cryptobank.backend.DTO.report;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsdcBuySellRatio {
	private Long totalTransactions;
    private List<Long> buyUsdcData;
    private List<Long> sellUsdcData;
}
