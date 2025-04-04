package com.cryptobank.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="debit_account")
public class DebitAccount {
	
	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="balance")
	private BigDecimal balance;
	
	@Column(name="delegated")
	private Boolean delegated;
	
	@Column(name="imported")
	private Boolean imported;
	
	@Column(name="wallet_index")
	private Integer walletIndex;
	
	@Column(name="created_at")
	private LocalDateTime createdA;
	
	@Column(name="first_verified_at")
	private LocalDateTime firstVerifiedAt;
	
	@Column(name="latest_verified_at")
	private LocalDateTime latestVerifiedAt;
	
	@Column(name="modified_at")
	private LocalDateTime modifiedAt;
	
	@Column(name="verified_at")
	private LocalDateTime verifiedAt;
	
	@Column(name="chain_type")
	private String chainType;
	
	@Column(name="connector_type")
	private String connectorType;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="custom_metadata")
	private String customMetadata;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="private_key")
	private String privateKey;
	
	@Column(name="recovery_method")
	private String recoveryMethod;
	
	@Column(name="wallet_address")
	private String walletAddress;
	
	@Column(name="wallet_client_type")
	private String walletClientType;
	
	//Ràng buộc với user với mối quan hệ 1-1
	@OneToOne
	@JoinColumn(name="user_id")
	private User userDebit;
	
}
