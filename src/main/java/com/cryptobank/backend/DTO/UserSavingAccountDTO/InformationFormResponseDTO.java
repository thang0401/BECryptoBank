package com.cryptobank.backend.DTO.UserSavingAccountDTO;

import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.Term;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationFormResponseDTO {
    private List<DebitWallet> debitWallets;
    private List<Term> terms;
}
