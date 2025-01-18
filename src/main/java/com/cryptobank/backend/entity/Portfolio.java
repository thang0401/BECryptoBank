package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "portfolio")
public class Portfolio {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "term_id")
    private Term term;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortfolioCategory category;

    @OneToMany(mappedBy = "portfolio")
    private Set<PortfolioDetail> portfolioDetail;

}
