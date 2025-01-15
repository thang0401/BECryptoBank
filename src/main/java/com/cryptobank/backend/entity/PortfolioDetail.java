package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "portfolio_detail")
public class PortfolioDetail {

    @Id
    private String id;
    private float amount;

    @ManyToOne
    @JoinColumn(name = "asset_type_id")
    private AssetType assetType;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

}
