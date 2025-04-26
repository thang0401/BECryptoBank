package com.cryptobank.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cryptobank.backend.DTO.CustomerReportDTO;
import com.cryptobank.backend.entity.CustomerReport;
import com.cryptobank.backend.entity.ReportCategory;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.repository.CustomerReportDAO;
import com.cryptobank.backend.repository.ReportCategoryDAO;
import com.cryptobank.backend.repository.StatusDAO;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("/api/CustomerReport")
@AllArgsConstructor
public class CustomerReportController {
    @Autowired
    CustomerReportDAO reportDAO;
    StatusDAO statusDAO;
    ReportCategoryDAO reportCategoryDAO;

    @GetMapping("/GetAll")
    public ResponseEntity<List<CustomerReport>> getAllCustomerReport() {
        List<CustomerReport> reportList=reportDAO.findAll();
        return ResponseEntity.ok(reportList);
    }

    @PostMapping("/IssueReport")
    public ResponseEntity<?> IssueReport(@RequestBody CustomerReportDTO entity) {
        //TODO: process POST request
        CustomerReport cusreport=initCustomerReport(entity);
        reportDAO.save(cusreport);
        return ResponseEntity.ok("successfull");
    }

    @PostMapping("path")
    public String ResolveReport(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }


    private CustomerReport initCustomerReport(CustomerReportDTO entity){
        String defaultStatusId="cvvvg2rme6nnaun2s4j0";
        //Validate valid status
        Status status=statusDAO.findById(defaultStatusId).orElse(null);
        if(status == null){
            System.out.println("Status not found!");
            return null;
        }
        //Validate valid category
        ReportCategory reportCategory=reportCategoryDAO.findById(entity.getCategoryID()).orElse(null);
        if(reportCategory==null){
            System.out.println("Category not found!");
            return null;
        }
       
        CustomerReport customerReport=new CustomerReport();
        customerReport.setId(UUID.randomUUID().toString());
        customerReport.setTitle(entity.getTitle());
        customerReport.setStatus(status);
        customerReport.setCategory(reportCategory);
        customerReport.setDescription(entity.getDescription());
        customerReport.setDocumentLink(entity.getDocumentLink());
        customerReport.setPriority(entity.getPriority());
        customerReport.setTransactionID(entity.getTransactionID());
        return customerReport; 
    }
    

    
}
