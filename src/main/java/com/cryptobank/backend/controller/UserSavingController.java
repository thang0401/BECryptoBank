package com.cryptobank.backend.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.UserSavingAccountDTO.InformationFormPostRequestDTO;
import com.cryptobank.backend.DTO.UserSavingAccountDTO.InformationFormResponseDTO;
import com.cryptobank.backend.entity.DebitAccount;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.entity.Term;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.SavingAccountDAO;
import com.cryptobank.backend.repository.TermDAO;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.services.generalServices.WithdrawService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("/user/saving")
public class UserSavingController {
    TermDAO termDAO;
    UserDAO userDAO;
    SavingAccountDAO savingAccountDAO;
    WithdrawService withdrawService;

    @GetMapping("/add-saving-asset")
    public ResponseEntity<InformationFormResponseDTO> getData(@RequestParam String userId) {
        List<Term> terms=getTerm();
        User user=getUserAccount(userId);
        List<DebitAccount> debitAccounts=getUserDebitAccounts(user);
        if(debitAccounts!=null){
        InformationFormResponseDTO informationFormResponseDTO=new InformationFormResponseDTO(debitAccounts,terms);
        return ResponseEntity.ok(informationFormResponseDTO);}
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/add-saving-asset")
    public ResponseEntity<?> postMethodName(@RequestParam(required = true) String userId,@RequestBody InformationFormPostRequestDTO entity) {
        //TODO: process POST request
        System.out.println(userId);
        System.out.println(entity.toString());
        User user=getUserAccount(userId);
        List<DebitAccount> debitAccounts=getUserDebitAccounts(user);
        DebitAccount account=null;
        for (DebitAccount debitAccount : debitAccounts) {
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
            newSavingAccount.setBalance(entity.getAmount());
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

    private List<DebitAccount> getUserDebitAccounts(User user){
        return user.getDebitAccounts();
    }

    private List<Term> getTerm(){
        return termDAO.findAll();
    }
}
