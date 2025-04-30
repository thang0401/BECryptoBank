package com.cryptobank.backend.repository;

import org.springframework.stereotype.Repository;

import com.cryptobank.backend.entity.CustomerReport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CustomerReportDAO extends JpaRepository<CustomerReport,String>{
    List<CustomerReport> findByReportedByAndDeleted(String userId,boolean deleted);

    
}
