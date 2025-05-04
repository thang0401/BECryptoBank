package com.cryptobank.backend.entity;


import java.time.OffsetTime;
import java.util.List;

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
     private String title;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "category_id")
     private ReportCategory category;

     @Column(name="description")
     private String description;
     
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "status_id")
     private Status status;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name="issue_to")
     private Employee issueTo;

     @Column(name="document_link")
     private List<String> documentLink=null;
     @Column(name="transaction_id")
     private String transactionID=null;
     @Column(name="priority")
     private int priority;
     @Column(name="reported_by")
     private String reportedBy;
     @Column(name="customer_email")
     private String customerEmail;
     @Column(name="customer_phone")
     private String customerPhone;
     @Column(name="contact_type")
     private String contactType;
     @Column(name="contact_time")
     private String contactTime;
}
