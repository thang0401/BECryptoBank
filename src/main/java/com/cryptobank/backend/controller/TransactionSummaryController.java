package com.cryptobank.backend.controller;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.report.TransactionStatusSummary;
import com.cryptobank.backend.DTO.report.TransactionSummary;
import com.cryptobank.backend.DTO.report.TransactionTrendDaily;
import com.cryptobank.backend.DTO.report.UsdcBuySellRatio;
import com.cryptobank.backend.services.TransactionReportService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequestMapping("/report-and-statistic/transaction-flow")
@RequiredArgsConstructor
public class TransactionSummaryController {
	private final TransactionReportService reportService;
	
	// Tổng quan các loại giao dịch có lọc từ ngày đến ngày.
	@GetMapping("/summary")
    public ResponseEntity<List<TransactionSummary>> getTransactionSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // Kiểm tra nếu startDate hoặc endDate không được truyền vào (null)
        if (startDate == null || endDate == null) {
            // Lấy thời gian hiện tại
            LocalDateTime now = LocalDateTime.now();
            // Thiết lập startDate là ngày 1 của tháng hiện tại, 00:00:00
            startDate = now.with(TemporalAdjusters.firstDayOfMonth())
                           .withHour(0).withMinute(0).withSecond(0).withNano(0);
            endDate = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        }

        // Kiểm tra tính hợp lệ của khoảng thời gian
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(null); 
        }

        // Gọi service để lấy dữ liệu
        List<TransactionSummary> summaries = reportService.getTransactionSummary(startDate, endDate);
        return ResponseEntity.ok(summaries);
    }
	
	//Tỷ lệ giao dịch thành công và thất bại 
	@GetMapping("/status-summary")
    public ResponseEntity<TransactionStatusSummary> getTransactionStatusSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // Kiểm tra nếu startDate hoặc endDate không được truyền vào (null)
        if (startDate == null || endDate == null) {
            LocalDateTime now = LocalDateTime.now();
            startDate = now.with(TemporalAdjusters.firstDayOfMonth())
                           .withHour(0).withMinute(0).withSecond(0).withNano(0);
            endDate = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }

        // Kiểm tra tính hợp lệ của khoảng thời gian
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(null);
        }


        try {
            TransactionStatusSummary summary = reportService.getTransactionStatusSummary(startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
	
	//Xu hướng giao dịch
	 @GetMapping("/trend-daily")
    public ResponseEntity<TransactionTrendDaily> getTransactionTrendDaily(
            @RequestParam(required = false) Integer targetMonth,
            @RequestParam(required = false) Integer targetYear) {

        // Thiết lập giá trị mặc định nếu không có tham số
        if (targetMonth == null || targetYear == null) {
            LocalDate now = LocalDate.now();
            targetMonth = now.getMonthValue();
            targetYear = now.getYear();
        }

        try {
            TransactionTrendDaily trend = reportService.getTransactionTrendDaily(targetMonth, targetYear);
            return ResponseEntity.ok(trend);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
	
	//Tỷ lệ mua/bán USDC
	 @GetMapping("/usdc-buy-sell-ratio")
    public ResponseEntity<UsdcBuySellRatio> getUsdcBuySellRatio(
            @RequestParam(required = false) Integer targetYear) {

        // Thiết lập giá trị mặc định nếu không có tham số
        if (targetYear == null) {
            targetYear = LocalDate.now().getYear();
        }

        try {
            UsdcBuySellRatio ratio = reportService.getUsdcBuySellRatio(targetYear);
            return ResponseEntity.ok(ratio);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
