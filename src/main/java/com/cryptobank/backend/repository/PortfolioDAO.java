// package com.cryptobank.backend.repository;

// import com.cryptobank.backend.entity.Portfolio;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface PortfolioDAO extends JpaRepository<Portfolio, String> {

// 	@Query(value="SELECT * FROM public.customer_portfolio WHERE customer_id= ?1 AND status IN ?2 "
// 			+ " ORDER BY ?3, portfolio_id"
// 			+ " LIMIT ?4 "
// 			+ " OFFSET ?5 ",nativeQuery = true)
// 	public List<Portfolio> PaginationOrderByPortfolioId(String customerId,List<String> status,String sortOption,int offset,int limit);

// }
