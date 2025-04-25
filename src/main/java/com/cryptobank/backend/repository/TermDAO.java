package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermDAO extends JpaRepository<Term,String> {
    public Optional<Term> findById(String id);
    public List<Term> findAll();
    public List<Term> findByDeleted(boolean deleted);
    public Term findByDeletedAndAmountMonth(boolean deleted,Long amountMonth);
}
