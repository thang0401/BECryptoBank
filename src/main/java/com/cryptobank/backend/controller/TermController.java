package com.cryptobank.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.TermAddDTO;
import com.cryptobank.backend.entity.Term;
import com.cryptobank.backend.entity.TermView;
import com.cryptobank.backend.repository.TermDAO;
import com.cryptobank.backend.repository.TermViewDAO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/term")
public class TermController {
    TermDAO termDAO;
    TermViewDAO termViewDAO;


    @GetMapping("/all-term")
    public ResponseEntity<?> getAllTermList(@RequestParam String param) {
        List<Term> list=termDAO.findAll();
        if(list!=null){
            return ResponseEntity.ok(list);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/term-active")
    public ResponseEntity<?> getActiveTermList(@RequestParam String param) {
        List<TermView> list=termViewDAO.findAll();
        if(list!=null){
            return ResponseEntity.ok(list);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/add-term")
    public ResponseEntity<?> addTerm(@RequestBody TermAddDTO entity) {
        //TODO: process POST request
        //Setup new instance of term
        String existId=checkIfExistAndActive(entity.getAmountMonth());
        if(!existId.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        Term term=new Term();
        term.setId(entity.getId());
        term.setAmount_month(entity.getAmountMonth());
        term.setType(entity.getType());
        term.setInterestRateOfMonth(entity.getInterestRate());
        //Save term
        try{
            termDAO.save(term);
        }catch(IllegalArgumentException ex){
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-term")
    public ResponseEntity<?> deleteTerm(@RequestBody String id) {
        //TODO: process POST request
        Term term=termDAO.findById(id).orElse(null);
        if(term!=null){
            term.setDeletedYN(true);
            termDAO.save(term);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/update-term-interest-rate")
    public ResponseEntity<?> updateTermInterestRate(@RequestBody TermAddDTO entity) {
        //TODO: process POST request
        Term term=termDAO.findById(entity.getId()).orElse(null);
        if(term!=null){
            term.setInterestRateOfMonth(entity.getInterestRate());
            return ResponseEntity.ok().body(null);
        }
        return ResponseEntity.notFound().build();
    }

    private String checkIfExistAndActive(Long amountMonth){
        String result=null;
        List<TermView> termViews=termViewDAO.findAll();
        for(int i=0;i<termViews.size()-1;i++){
            TermView termView=termViews.get(i);
            if(termView.getAmountMonth()==amountMonth){
                result=termView.getId();
            }
        }
        return result;
    }
    
    
    
    


    


}
