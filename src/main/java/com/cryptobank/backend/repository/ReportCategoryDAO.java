package com.cryptobank.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptobank.backend.entity.ReportCategory;

public interface ReportCategoryDAO extends JpaRepository<ReportCategory,String> {
    
}
