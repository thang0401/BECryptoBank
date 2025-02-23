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
@Table(name = "function_permission")
public class FunctionPermission extends BaseEntity {

    @Column(name= "name")
    private String name;

    @Column(name="all_yn")
    private Boolean allEnable;

    @Column(name="insert_yn")
    private Boolean insertable;

    @Column(name="update_yn")
    private Boolean updatable;

    @Column(name="view_yn")
    private Boolean viewalbe;

    @Column(name="view_all_yn")
    private Boolean viewAllAble;

    @Column(name="excel_yn")
    private Boolean excelAble;

    @Column(name="delete_permission_yn")
    private Boolean deletePermission;

    @Column(name="function_id")
    private String functionId;

}
