package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="function_url")
public class FunctionURL extends BaseEntity {

    @Column(name="function_name_url")
    private String functionNameUrl;

    @Column(name="role_id")
    private String roleId;

    @Column(name="is_activated")
    private boolean activated;
    
}
