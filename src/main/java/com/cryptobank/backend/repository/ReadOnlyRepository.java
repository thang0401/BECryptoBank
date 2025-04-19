package com.cryptobank.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springdoc.core.converters.models.Pageable;
import org.springdoc.core.converters.models.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ReadOnlyRepository<T,ID> extends JpaRepository<T,ID> {

    List<T> findAll();

    List<T> findAll(Sort sort);

    Page<T> findAll(Pageable page);

    Optional<T> findById(ID id);

    long count();
}
