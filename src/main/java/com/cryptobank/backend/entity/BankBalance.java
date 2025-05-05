package com.cryptobank.backend.entity;

import com.cryptobank.backend.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bank_balance")
@Data
@NoArgsConstructor
public class BankBalance {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id = IdGenerator.generate();

    @Column(name = "usdc_balance")
    private BigDecimal usdcBalance;

    @Column(name = "vnd_balance")
    private BigDecimal vndBalance;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}