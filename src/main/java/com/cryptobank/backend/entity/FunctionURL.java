package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
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
