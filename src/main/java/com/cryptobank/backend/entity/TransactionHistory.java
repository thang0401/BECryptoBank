package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Subselect;

import java.time.LocalDateTime;

@Data
@Entity
@Subselect("select d.id, d.from_pubkey as sent, d.portfolio_id as received, d.created_date as createdDate, 'Deposit' as type from deposit_portfolio_history d union all " +
        "select w.id, w.from_portfolio_id as sent, w.received_pubkey as received, w.created_date as createdDate, 'Withdraw' as type from withdraw_portfolio_history w union all " +
        "select t.id, t.send_portfolio_id as sent, t.received_portfolio_id as received, t.created_date as createdDate, 'Transfer' as type from transfer_portfolio_history t " +
        "order by createdDate")
public class TransactionHistory {

    @Id
    private String id;
    private String type;
    private String sent;
    private String received;
    @Column(name = "createddate")
    private LocalDateTime createdDate;

}
