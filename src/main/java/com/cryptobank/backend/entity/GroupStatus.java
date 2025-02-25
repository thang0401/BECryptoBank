package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="group_status")
@AllArgsConstructor
@NoArgsConstructor
public class GroupStatus {
    @Id
    private String id;

    @Column(name="group_name")
    private String groupName;

    @ManyToOne
    @JoinColumn(name="status_id")
    private Status status;

    @Column(name="create_at")
    private ZonedDateTime createdAt;
}
