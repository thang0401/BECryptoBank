package com.cryptobank.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cryptobank.backend.entity.ReportCategory;

public interface ReportCategoryDAO extends JpaRepository<ReportCategory,String> {
    @Query("SELECT DISTINCT e.Title FROM ReportCategory e")
    List<String> findDistinctTitle();
}
