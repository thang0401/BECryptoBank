package com.cryptobank.backend.DTO.SavingHeirFormDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddHeirFormDTO {
    
    private String savingAccountId;
    private String heirId;
    private String ggDriveLink;
}
