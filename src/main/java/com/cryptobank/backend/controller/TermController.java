package com.cryptobank.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.TermAddDTO;
import com.cryptobank.backend.entity.Term;
import com.cryptobank.backend.entity.TermView;
import com.cryptobank.backend.repository.TermDAO;
import com.cryptobank.backend.repository.TermViewDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@CrossOrigin("*")
@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/term")
@Tag(name = "Term", description = "Term controller")
public class TermController {
    @Autowired
    TermDAO termDAO;
    TermViewDAO termViewDAO;

    //Get all terms
    @GetMapping("/all-term")
    public ResponseEntity<?> getAllTermList() {
        List<Term> list=termDAO.findAll();
        if(list!=null){
            return ResponseEntity.ok(list);
        }
        return ResponseEntity.notFound().build();
    }
    
    //Get active terms only
    @GetMapping("/terms-active")
    public ResponseEntity<?> getActiveTermList() {
        List<Term> list=termDAO.findByDeleted(false);
        if(list!=null){
            return ResponseEntity.ok(list);
        }
        return ResponseEntity.notFound().build();
    }

    //Add a new term
    @PostMapping("/add-term")
    public ResponseEntity<?> addTerm( @Parameter(description = "Data lấy từ client") @RequestBody TermAddDTO entity) {
        //Setup new instance of term
        String existId=checkIfExistAndActive(entity.getAmountMonth());
        
        if(!existId.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Term term=new Term();
        term.setAmountMonth(entity.getAmountMonth());
        term.setType(entity.getType());
        term.setInterestRate(entity.getInterestRate());
        term.setMinimum(entity.getMinimum());
        //Save term
        try{
            termDAO.save(term);
            entity.setId(term.getId());
        }catch(IllegalArgumentException ex){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(entity);
    }
    @DeleteMapping("/delete-term")
    public ResponseEntity<?> deleteTerm( @Parameter(description = "ID của term") @RequestBody String id) {
        //TODO: process POST request
        Term term=termDAO.findById(id).orElse(null);
        if(term!=null){
            System.out.println(term.getId());
            term.setDeleted(true);
            termDAO.save(term);
            return ResponseEntity.ok().build();
        }
        System.out.println("No term found for id: "+id);
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update-term-interest-rate")
    public ResponseEntity<?> updateTermInterestRate( @Parameter(description = "Data lấy từ client") @RequestBody TermAddDTO entity) {
        //TODO: process POST request
        System.out.println(entity.getId());
        Term term=termDAO.findById(entity.getId()).orElse(null);
        if(term!=null){
            term.setInterestRate(entity.getInterestRate());
            term.setMinimum(entity.getMinimum());
            termDAO.save(term);
            TermAddDTO resObj= mapTermToTermDTO(term);
            return ResponseEntity.ok().body(resObj);
        }
        return ResponseEntity.notFound().build();
    }

    private String checkIfExistAndActive(Long amountMonth){
        String result="";
        Term term=termDAO.findByDeletedAndAmountMonth(false,amountMonth);
        if(term!=null){
            result=term.getId();
        }
        return result;
    }

    private TermAddDTO mapTermToTermDTO(Term term){
        TermAddDTO termDTO=new TermAddDTO();
        termDTO.setId(term.getId());
        termDTO.setAmountMonth(term.getAmountMonth());
        termDTO.setCreatedBy(term.getCreatedBy());
        termDTO.setInterestRate(term.getInterestRate());
        termDTO.setType(term.getType());
        termDTO.setMinimum(term.getMinimum());
        return termDTO;
    }


}
