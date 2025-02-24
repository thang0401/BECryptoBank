package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="function_url")
public class FunctionURL {
    @Id
    private String id;

    @Column(name="function_name_url")
    private String functionNameUrl;

    @Column(name="role_id")
    private String roleId;

    @Column(name="is_activated")
    private Boolean isActivated;

    @Column(name="created_date")
    private ZonedDateTime createdDate;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="modified_date")
    private ZonedDateTime modifiedDate;

    @Column(name="modified_by")
    private String modifiedBy;
    
}
