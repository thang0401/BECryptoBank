package com.cryptobank.backend.controller;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.TransactionSummary;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.services.generalServices.TransactionReportService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class TransactionSummaryController {
	private final TransactionReportService reportService;
	

	@GetMapping("/transaction-summary")
    public ResponseEntity<ApiResponse<List<TransactionSummary>>> getTransactionSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {

      
		// Kiểm tra nếu startDate hoặc endDate không được truyền vào (null)
        if (startDate == null || endDate == null) {
            // Lấy thời gian hiện tại theo múi giờ hệ thống
            ZonedDateTime now = ZonedDateTime.now();
            // Thiết lập startDate là ngày 1 của tháng hiện tại, 00:00:00
            // TemporalAdjusters.firstDayOfMonth() đưa về ngày đầu tháng
            // withHour(0).withMinute(0)... đặt giờ về 00:00:00.000
            startDate = now.with(TemporalAdjusters.firstDayOfMonth())
                           .withHour(0).withMinute(0).withSecond(0).withNano(0);
            // Thiết lập endDate là ngày 1 của tháng tiếp theo, 00:00:00
            // plusMonths(1) tăng lên 1 tháng, sau đó lấy ngày đầu tháng
            endDate = now.plusMonths(1)
                         .with(TemporalAdjusters.firstDayOfMonth())
                         .withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        // Gọi service để lấy dữ liệu
        List<TransactionSummary> summaries = reportService.getTransactionSummary(startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>("", summaries));
    }
}
