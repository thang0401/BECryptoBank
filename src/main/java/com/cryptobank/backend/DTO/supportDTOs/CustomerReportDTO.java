package com.cryptobank.backend.DTO.supportDTOs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerReportDTO {
    @JsonProperty("category")
    private String category;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("priority")
    private String priority;
    @JsonProperty("description")
    private String description;
    @JsonProperty("subject")
    private String subject;
    private String reportedBy;
    // @JsonProperty("files")
    private List<String> documentLink=null;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")  
    private String phone;
    @JsonProperty("contactTime")
    private String contactTime;
    @JsonProperty("contactType")
    private String contactType;
}
