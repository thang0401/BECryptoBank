package com.cryptobank.backend.DTO;

import java.util.List;

import com.cryptobank.backend.entity.DebitAccount;
import com.cryptobank.backend.entity.Term;

import lombok.Data;

@Data
public class SavingResponseDataDTO {
    private List<Term> terms;
    private List<DebitAccount> userDebitAccounts;
}
