package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.UserSavingAccountDTO.InformationFormPostRequestDTO;

import com.cryptobank.backend.DTO.UserSavingAccountDTO.InformationFormResponseDTO;
import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.entity.Term;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.SavingAccountDAO;
import com.cryptobank.backend.repository.TermDAO;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.services.generalServices.WithdrawService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/api/user/saving")
public class UserSavingController {
    TermDAO termDAO;
    UserDAO userDAO;
    SavingAccountDAO savingAccountDAO;
    DebitWalletDAO debitWalletDAO;
    WithdrawService withdrawService;

    

    @GetMapping("/add-saving-asset")
    public ResponseEntity<ApiResponse<InformationFormResponseDTO>> getData(@RequestParam String userId) {
        List<Term> terms=getTerm();
        User user=getUserAccount(userId);
        List<DebitWallet> debitWallets=getUserDebitWallets(user);
        if(debitWallets!=null){
        InformationFormResponseDTO informationFormResponseDTO=new InformationFormResponseDTO(debitWallets,terms);
        return ResponseEntity.ok(new ApiResponse<>("", informationFormResponseDTO));}
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/add-saving-asset")
    public ResponseEntity<?> postMethodName(@RequestParam(required = true) String userId,@RequestBody InformationFormPostRequestDTO entity) {
        //TODO: process POST request
        System.out.println(userId);
        System.out.println(entity.toString());
        User user=getUserAccount(userId);
        List<DebitWallet> debitWallets=getUserDebitWallets(user);
        DebitWallet account=null;
        for (DebitWallet debitAccount : debitWallets) {
            System.out.println(debitAccount.getId());
            if(debitAccount.getId().equals(entity.getDebitAccountId())){
                account=debitAccount;
            }
        }

        //Get selected Term instance
        Term selectedTerm=termDAO.findById(entity.getTermId()).orElse(null);

        //Get provided OTP
        Integer OTP=provideOTP();


        Boolean checkValidBalance=withdrawService.checkValidBalance(account, entity.getAmount());
        Boolean checkValidOTP=entity.getOTP().equals(OTP);

        //Check valid balance
        if(!checkValidBalance){
            return ResponseEntity.badRequest().body("Not succesful causing by insufficient balance");             
        }

        //Check valid OTP
        if(!checkValidOTP){
            return ResponseEntity.badRequest().body("Invalid OTP");     
        }

        if(checkValidBalance&&checkValidOTP){
            UUID uuid=UUID.randomUUID();
            //Save to DB
            SavingAccount newSavingAccount=new SavingAccount();
            newSavingAccount.setBalance(BigDecimal.valueOf(entity.getAmount()));
            // newSavingAccount.setHeirStatus(false);
            newSavingAccount.setInterestRate(selectedTerm.getInterestRateOfMonth());
            // newSavingAccount.setCreatedBy(userId);
            // newSavingAccount.setCreatedDate(ZonedDateTime.now());
            newSavingAccount.setMaturityDate(null);
            newSavingAccount.setUser(user);
            newSavingAccount.setId(uuid.toString());
            // newSavingAccount.setUuid(uuid);
            newSavingAccount.setTerm(selectedTerm);
            savingAccountDAO.save(newSavingAccount);
            //Reduce balance
            withdrawService.WithdrawIntoSavingAccount(account, entity.getAmount());
            //Response OK
            return ResponseEntity.ok("Successful"); 
        }
        return ResponseEntity.badRequest().body("Not succesful causing by insufficient balance"); 
    }
    
    private Integer provideOTP(){
        return 123456;
    }

    private User getUserAccount(String userId){  
        User user=userDAO.findById(userId).orElse(null);
        return user;
    }

    private List<DebitWallet> getUserDebitWallets(User user){
        return debitWalletDAO.findByUserId(user.getId());
    }

    private List<Term> getTerm(){
        return termDAO.findAll();
    }
}
