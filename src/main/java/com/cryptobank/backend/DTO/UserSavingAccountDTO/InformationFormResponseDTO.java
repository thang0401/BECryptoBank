package com.cryptobank.backend.DTO.UserSavingAccountDTO;

import java.util.List;

import com.cryptobank.backend.entity.DebitAccount;
import com.cryptobank.backend.entity.Term;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationFormResponseDTO {
    private List<DebitAccount> debitAccounts;
    private List<Term> terms;
}
