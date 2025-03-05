package com.cryptobank.backend.repository;

import org.springframework.data.repository.NoRepositoryBean;

import com.cryptobank.backend.entity.TermView;

@NoRepositoryBean
public interface TermViewDAO extends ReadOnlyRepository<TermView,String> {
    

}
