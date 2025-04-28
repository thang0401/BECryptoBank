package com.cryptobank.backend.services;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cryptobank.backend.DTO.report.TransactionStatus;
import com.cryptobank.backend.DTO.report.TransactionStatusSummary;
import com.cryptobank.backend.DTO.report.TransactionSummary;
import com.cryptobank.backend.DTO.report.TransactionTrendDaily;
import com.cryptobank.backend.DTO.report.UsdcBuySellRatio;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Service
public class TransactionReportService {
    @PersistenceContext
    private final EntityManager entityManager;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransactionReportService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<TransactionSummary> getTransactionSummary(LocalDateTime startDate, LocalDateTime endDate) {
        // Tạo truy vấn native SQL để gọi hàm get_transaction_type_summary
        String sql = "SELECT * FROM public.get_transaction_type_summary(:startDate, :endDate)";
        var query = entityManager.createNativeQuery(sql);

        // Đặt tham số
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        // Lấy kết quả và ánh xạ sang TransactionSummary
        List<Object[]> results = query.getResultList();
        return results.stream().map(row -> new TransactionSummary(
                (String) row[0], // transaction_type (text)
                ((Number) row[1]).longValue(), // transaction_count (bigint)
                new java.math.BigDecimal(row[2].toString()), // percentage (numeric)
                (String) row[3] // color (text)
        )).collect(Collectors.toList());
    }
    
    public TransactionStatusSummary getTransactionStatusSummary(LocalDateTime startDate, LocalDateTime endDate) {
        // Kiểm tra đầu vào
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate must not be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate");
        }

        try {
            // Tạo truy vấn native SQL
            String sql = "SELECT public.get_transaction_status_summary(:startDate, :endDate)";
            var query = entityManager.createNativeQuery(sql);

            // Đặt tham số
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);


            // Lấy kết quả JSON (dạng chuỗi)
            String jsonResult = (String) query.getSingleResult();

            // Chuyển đổi JSON thành Map
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = objectMapper.readValue(jsonResult, Map.class);

            // Lấy totalTransactions
            Long totalTransactions = ((Number) resultMap.get("totalTransactions")).longValue();

            // Lấy transactionStatuses
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> statusList = (List<Map<String, Object>>) resultMap.get("transactionStatuses");
            List<TransactionStatus> transactionStatuses = statusList.stream().map(status -> new TransactionStatus(
                    (String) status.get("title"),
                    ((Number) status.get("amount")).longValue(),
                    (String) status.get("avatarColor")
            )).collect(Collectors.toList());

            // Tạo đối tượng TransactionStatusSummary
            return new TransactionStatusSummary(totalTransactions, transactionStatuses);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch transaction status summary", e);
        }
    }
    
    public TransactionTrendDaily getTransactionTrendDaily(Integer targetMonth, Integer targetYear) {
        // Kiểm tra đầu vào
        if (targetMonth == null || targetYear == null) {
            throw new IllegalArgumentException("targetMonth and targetYear must not be null");
        }
        if (targetMonth < 1 || targetMonth > 12) {
            throw new IllegalArgumentException("targetMonth must be between 1 and 12");
        }
        if (targetYear < 1900 || targetYear > 9999) {
            throw new IllegalArgumentException("targetYear must be a valid year");
        }

        try {
            // Tạo truy vấn native SQL
            String sql = "SELECT public.get_transaction_trend_daily(:targetMonth, :targetYear)";
            var query = entityManager.createNativeQuery(sql);
            query.setParameter("targetMonth", targetMonth);
            query.setParameter("targetYear", targetYear);


            // Lấy kết quả JSON (dạng chuỗi)
            String jsonResult = (String) query.getSingleResult();

            // Chuyển đổi JSON thành Map
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = objectMapper.readValue(jsonResult, Map.class);

            // Ánh xạ các trường
            Integer month = (Integer) resultMap.get("month");
            Long totalAmount = ((Number) resultMap.get("totalAmount")).longValue();
            BigDecimal growthPercentage = new BigDecimal(resultMap.get("growthPercentage").toString());
            @SuppressWarnings("unchecked")
            List<Long> trendData = ((List<Object>) resultMap.get("trendData"))
                    .stream()
                    .map(obj -> ((Number) obj).longValue())
                    .collect(Collectors.toList());

            // Tạo đối tượng TransactionTrendDaily
            return new TransactionTrendDaily(month, totalAmount, growthPercentage, trendData);
        } catch (Exception e) {
               throw new RuntimeException("Failed to fetch transaction trend daily", e);
        }
    }
    
    public UsdcBuySellRatio getUsdcBuySellRatio(Integer targetYear) {
        // Kiểm tra đầu vào
        if (targetYear == null) {
            throw new IllegalArgumentException("targetYear must not be null");
        }
        if (targetYear < 1900 || targetYear > 9999) {
            throw new IllegalArgumentException("targetYear must be a valid year");
        }

        try {
            // Tạo truy vấn native SQL
            String sql = "SELECT public.get_usdc_buy_sell_ratio(:targetYear)";
            var query = entityManager.createNativeQuery(sql);
            query.setParameter("targetYear", targetYear);

            // Lấy kết quả JSON
            String jsonResult = (String) query.getSingleResult();

            // Chuyển đổi JSON thành Map
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = objectMapper.readValue(jsonResult, Map.class);

            // Ánh xạ các trường
            Long totalTransactions = ((Number) resultMap.get("totalTransactions")).longValue();
            @SuppressWarnings("unchecked")
            List<Long> buyUsdcData = ((List<Object>) resultMap.get("buyUsdcData"))
                    .stream()
                    .map(obj -> ((Number) obj).longValue())
                    .collect(Collectors.toList());
            @SuppressWarnings("unchecked")
            List<Long> sellUsdcData = ((List<Object>) resultMap.get("sellUsdcData"))
                    .stream()
                    .map(obj -> ((Number) obj).longValue())
                    .collect(Collectors.toList());

            // Tạo đối tượng UsdcBuySellRatio
            return new UsdcBuySellRatio(totalTransactions, buyUsdcData, sellUsdcData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch USDC buy/sell ratio", e);
        }
    }
}