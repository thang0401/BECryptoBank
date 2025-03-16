package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermDAO extends JpaRepository<Term,String> {
    public List<Term> findAll();
}
