package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "function_permission")
public class FunctionPermission {
    @Id
    private String id;

    @Column(name= "name")
    private String name;

    @Column(name="all_yn")
    private Boolean allEnable;

    @Column(name="insert_yn")
    private Boolean insertable;

    @Column(name="update_yn")
    private Boolean updatable;

    @Column(name="delete_yn")
    private Boolean deletable;

    @Column(name="view_yn")
    private Boolean viewalbe;

    @Column(name="view_all_yn")
    private Boolean viewAllAble;

    @Column(name="excel_yn")
    private Boolean excelAble;

    @Column(name="delete_permission_yn")
    private Boolean deletePermission;

    @Column(name="created_date")
    private ZonedDateTime createdDate;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="modified_date")
    private ZonedDateTime modifiedDate;

    @Column(name="modified_by")
    private String modifiedBy;

    @Column(name="function_id")
    private String functionId;


}
