package com.cryptobank.backend.repository;

import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cryptobank.backend.DTO.TransactionSummary;

@Repository
public interface TransactionSummaryDAO {
    @Query(value = "SELECT * FROM get_transaction_summary(:startDate, :endDate)", nativeQuery = true)
    List<TransactionSummary> getTransactionSummary(
        @Param("startDate") ZonedDateTime startDate,
        @Param("endDate") ZonedDateTime endDate
    );
}