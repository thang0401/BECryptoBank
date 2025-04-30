package com.cryptobank.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptobank.backend.repository.ReportCategoryDAO;

@Service
public class CustomerReportServices {
    @Autowired
    ReportCategoryDAO reportCategoryDAO;

    public List<String> getUniqueCategoryName(){
        List<String> unique= reportCategoryDAO.findDistinctTitle();
        return unique;
    }   
}
