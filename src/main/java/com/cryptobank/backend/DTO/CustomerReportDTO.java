package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class CustomerReportDTO {
    private String categoryID;
    private String transactionID;
    private int    priority;
    private String description;
    private String title;
    private String reportedBy;
    private String documentLink;
}
