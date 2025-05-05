package com.cryptobank.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.cryptobank.backend.services.CustomerReportServices;
import com.cryptobank.backend.services.MinioService;
import com.cryptobank.backend.utils.JwtUtil;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;




@RestController
@RequestMapping("/api/CustomerReport")
@AllArgsConstructor
public class CustomerReportController {
    @Autowired
    CustomerReportDAO reportDAO;
    StatusDAO statusDAO;
    ReportCategoryDAO reportCategoryDAO;
    CustomerReportServices cusreportService;
    MinioService minioService;
    JwtUtil jwtUtil;


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
    public ResponseEntity<?> IssueReport(@AuthenticationPrincipal UserDetails userDetails,@ModelAttribute CustomerReportDTO entity,@RequestParam MultipartFile[] files) {
        //TODO: process POST request
        if(userDetails!=null){
            String userId=userDetails.getUsername();
        if(userId!=null){
            entity.setReportedBy(userId);
        }
        }
        if(files!=null){
           List<String> uploadLocationLink= HandleReceiveFile(files);
           entity.setDocumentLink(uploadLocationLink);
           System.out.println("Have files and upload it to: "+uploadLocationLink);
        }else{
            System.out.println("Do not have any additional file");
        }
        CustomerReport cusreport=initCustomerReport(entity);
        if(cusreport==null){
            return ResponseEntity.badRequest().build();
        }
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
      

    @PostMapping("/ChangeStatus")
    public ResponseEntity<?> ChangeStatusRequest(@RequestParam String statusName,@RequestBody String reportId) {
        //TODO: process POST request
        Boolean result=ChangeStatus(statusName, reportId);
        if(result==true){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    

    @PostMapping("/ResolveReport")
    public ResponseEntity<?> ResolveReport(@RequestBody String reportId) {
        //TODO: process POST request
        String resolveStatusName="";
        Boolean result=ChangeStatus(resolveStatusName, reportId);
        if(result==true){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    public record Issue(
        String id,
        String name
    ){}
    @GetMapping("/initNewReqForm")
    public ResponseEntity<?> getInitForm() {
        List<ReportCategory> listCate=reportCategoryDAO.findAll();
        Map<String,ArrayList<Record>> map=new HashMap<>();
        for (ReportCategory reportIssue : listCate) {
            Issue issue=new Issue(reportIssue.getId(), reportIssue.getIssue());
            if(map.get(reportIssue.getTitle())==null){
                ArrayList<Record> array=new ArrayList<>();
                array.add(issue);
                map.put(reportIssue.getTitle(), array);
            }else{
                map.get(reportIssue.getTitle()).add(issue);
            }
        }
        
        return ResponseEntity.ok(map);
    }
    

    //Helper
    private CustomerReport initCustomerReport(CustomerReportDTO entity){
        String defaultStatusId="cvvvg2rme6nnaun2s4j0";
        //Validate valid status
        Status status=statusDAO.findById(defaultStatusId).orElse(null);
        if(status == null){
            System.out.println("Status not found!");
            return null;
        }

        //Validate valid category
        System.out.println(entity.getCategory());
        ReportCategory reportCategory=reportCategoryDAO.findById(entity.getCategory()).orElse(null);
        if(reportCategory==null){
            System.out.println("Category not found!");
            return null;
        }

        Map<String,Integer> priorityMap=new HashMap<>();
        priorityMap.put("medium", 2);
        priorityMap.put("high",3);
        priorityMap.put("low",1);
        priorityMap.put("critical",4);
        
        CustomerReport customerReport=new CustomerReport();
        customerReport.setId(UUID.randomUUID().toString());
        customerReport.setTitle(entity.getSubject());
        customerReport.setStatus(status);
        customerReport.setCategory(reportCategory);
        customerReport.setDescription(entity.getDescription());
        customerReport.setReportedBy(entity.getReportedBy());
        customerReport.setDocumentLink(entity.getDocumentLink());
        customerReport.setPriority(priorityMap.get(entity.getPriority()));
        customerReport.setTransactionID(entity.getOrderId());
        customerReport.setCustomerEmail(entity.getEmail());
        customerReport.setCustomerPhone(entity.getPhone());
        customerReport.setContactTime(entity.getContactTime());
        customerReport.setContactType(entity.getContactType());
        return customerReport; 
    }
    
    private List<String> HandleReceiveFile(MultipartFile[] files){
        List<String> uploadLocation=new ArrayList<>();
        String uuid=UUID.randomUUID().toString();
        for(MultipartFile file:files){
            try {
                InputStream fileInputStream=file.getInputStream();
                uploadLocation.add(minioService.uploadFile(uuid, "customer-report", fileInputStream, file.getContentType()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Failed to open input file");
                continue;
            }
        }
        return uploadLocation;
    }

    private Boolean ChangeStatus(String statusName,String reportId){
        CustomerReport customerReport=reportDAO.findById(reportId).orElse(null);
        Status getStatus=statusDAO.findByName(statusName).orElse(null);
        if(customerReport!=null&&getStatus!=null){
            customerReport.setStatus(getStatus);
            reportDAO.save(customerReport);
            return true;
        }
        return false;
    }

}
