package com.cryptobank.backend.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "heir")
public class Heir {

	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="id_card")
	private String id_card;
	
	@Column(name="last_words")
	private String last_words;
	
	@Column(name="date_of_birth")
	private String date_of_birth;
	
	@Column(name="delete_yn")
	private String delete_yn;
	
	@Column(name="created_date")
	private LocalDateTime created_date;

	@Column(name="modified_date")
	private LocalDateTime modified_date;
	
	@Column(name="modified_by")
	private String modified_by;
	
	@ManyToOne
    @JoinColumn(name = "created_by")
    private User createdByUser;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortfolioCategory portfolioCategory;


}
