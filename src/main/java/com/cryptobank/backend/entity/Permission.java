package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "all_yn")
    private boolean all;

    @Column(name = "insert_yn")
    private boolean insert;

    @Column(name = "update_yn")
    private boolean update;

    @Column(name = "delete_yn")
    private boolean delete;

    @Column(name = "view_yn")
    private boolean view;

    @Column(name = "view_all_yn")
    private boolean viewAll;

    @Column(name = "excel_yn")
    private boolean excel;

    @Column(name = "delete_permission_yn")
    private boolean deletePermission;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

}
