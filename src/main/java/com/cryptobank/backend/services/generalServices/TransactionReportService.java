package com.cryptobank.backend.services.generalServices;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cryptobank.backend.repository.TransactionSummaryDAO;

import lombok.RequiredArgsConstructor;
import com.cryptobank.backend.DTO.TransactionSummary;

@RequiredArgsConstructor
@Service
public class TransactionReportService {
 
	private final TransactionSummaryDAO summaryDAO ;

    public List<TransactionSummary> getTransactionSummary(ZonedDateTime startDate, ZonedDateTime endDate) {
        return summaryDAO.getTransactionSummary(startDate, endDate);
    }
}
