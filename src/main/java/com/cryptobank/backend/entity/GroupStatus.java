package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="group_status")
public class GroupStatus extends BaseEntity {

    @Column(name="group_name")
    private String groupName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="status_id")
    private Status status;

}
