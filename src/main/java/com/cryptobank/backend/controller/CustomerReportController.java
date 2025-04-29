package com.cryptobank.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cryptobank.backend.DTO.supportDTOs.CustomerReportDTO;
import com.cryptobank.backend.DTO.supportDTOs.responseDTOs.GetAllDTO;
import com.cryptobank.backend.DTO.supportDTOs.responseDTOs.UserListIssueResponseDTO;
import com.cryptobank.backend.entity.CustomerReport;
import com.cryptobank.backend.entity.ReportCategory;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.repository.CustomerReportDAO;
import com.cryptobank.backend.repository.ReportCategoryDAO;
import com.cryptobank.backend.repository.StatusDAO;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
@RequestMapping("/api/CustomerReport")
@AllArgsConstructor
public class CustomerReportController {
    @Autowired
    CustomerReportDAO reportDAO;
    StatusDAO statusDAO;
    ReportCategoryDAO reportCategoryDAO;

    @GetMapping("/GetAll")
    public ResponseEntity<?> getAllCustomerReport() {
        List<CustomerReport> reportList=reportDAO.findAll();
        List<GetAllDTO> jsonSerilableReportList=new ArrayList<>();
        //Create DTO for custom response cause\
        for (CustomerReport customerReport : reportList) {
            GetAllDTO getAllDTO=new GetAllDTO();
            getAllDTO.setId(customerReport.getId());
            getAllDTO.setTitle(customerReport.getTitle());
            getAllDTO.setIssue(customerReport.getCategory().getIssue());
            getAllDTO.setPriority(customerReport.getPriority());
            getAllDTO.setReported_by(customerReport.getReportedBy());
            getAllDTO.setStatus(customerReport.getStatus().getNote());
            getAllDTO.setCreated_date(customerReport.getCreatedAt().toString());
            jsonSerilableReportList.add(getAllDTO);
        } 
        return ResponseEntity.ok(jsonSerilableReportList);
    }

    @PostMapping("/IssueReport")
    public ResponseEntity<?> IssueReport(@RequestBody CustomerReportDTO entity) {
        //TODO: process POST request
        CustomerReport cusreport=initCustomerReport(entity);
        reportDAO.save(cusreport);
        return ResponseEntity.ok("successfull");
    }

    @GetMapping("/User/Report")
    public ResponseEntity<?> getUserReport(@RequestBody String userid) {
        List<CustomerReport> userReportList=reportDAO.findByReportedByAndDeleted(userid, false);
        if(userReportList.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        List<UserListIssueResponseDTO> jsonSerilableReportList=new ArrayList<>();
        //Create DTO for custom response cause\
        for (CustomerReport customerReport : userReportList) {
            //New DTO
            UserListIssueResponseDTO ULIRDTO=new UserListIssueResponseDTO();
            ULIRDTO.setTitle(customerReport.getTitle());
            ULIRDTO.setIssue(customerReport.getCategory().getIssue());
            ULIRDTO.setCreated_date(customerReport.getCreatedAt().toString());
            ULIRDTO.setStatus(customerReport.getStatus().getNote());
            //List add
            jsonSerilableReportList.add(ULIRDTO);
        } 
        return ResponseEntity.ok(jsonSerilableReportList);
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
        customerReport.setReportedBy(entity.getReportedBy());
        customerReport.setDocumentLink(entity.getDocumentLink());
        customerReport.setPriority(entity.getPriority());
        customerReport.setTransactionID(entity.getTransactionID());
        return customerReport; 
    }
    

    
}
