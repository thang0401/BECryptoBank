package com.cryptobank.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptobank.backend.entity.Term;

@Repository
public interface TermDAO extends JpaRepository<Term,String> {
    public List<Term> findAll();
}
