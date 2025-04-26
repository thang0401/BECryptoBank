package com.cryptobank.backend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="customer_issue_report")
public class CustomerReport extends BaseEntity{
     @Column(name="title")
     private String Title;
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "category_id")
     private ReportCategory Category;
     @Column(name="description")
     private String Description;
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "status_id")
     private Status Status;
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name="issue_to")
     private Employee IssueTo;
     @Column(name="document_link")
     private String DocumentLink=null;
     @Column(name="transaction_id")
     private String TransactionID=null;
     @Column(name="priority")
     private int Priority;
}
