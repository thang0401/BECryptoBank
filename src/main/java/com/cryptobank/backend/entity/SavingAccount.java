package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="saving_account")
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccount {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name="term_id")
    private Term term;

    @Column(name="note")
    private String note;

    @Column(name="status_id")
    private String statusId;

    @Column(name="delete_yn")
    private Boolean isDeleted;

    @Column(name="created_date")
    private ZonedDateTime createdDate;

    @Column(name="created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private ZonedDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name="balance")
    private Double balance;

    @Column(name="interest_rate")
    private Double interestRate;

    @Column(name="maturity_date")
    private ZonedDateTime maturityDate;

    @Column(name = "gg_drive_url")
    private String ggDriveUrl;

    @Column(name = "heir_status")
    private Boolean heirStatus;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "savingAccount",cascade = CascadeType.ALL)
    private List<SavingTransaction> transactions; 
}
