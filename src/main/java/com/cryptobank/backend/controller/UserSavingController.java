package com.cryptobank.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import com.cryptobank.backend.DTO.UserSavingAccountDTO.InformationFormPostRequestDTO;
import com.cryptobank.backend.DTO.UserSavingAccountDTO.InformationFormResponseDTO;
import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.entity.Term;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.SavingAccountDAO;
import com.cryptobank.backend.repository.TermDAO;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.services.WithdrawService;
import com.cryptobank.backend.smartcontract.SavingAccountTest;
import com.cryptobank.backend.services.AccruedInterestService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/user/saving")
public class UserSavingController {
   TermDAO termDAO;
   UserDAO userDAO;
   SavingAccountDAO savingAccountDAO;
   DebitWalletDAO debitWalletDAO;
   WithdrawService withdrawService;
   AccruedInterestService accruedInterestService;


   @GetMapping("/add-saving-asset")
   public ResponseEntity<InformationFormResponseDTO> getData(@RequestParam String userId) {
       List<Term> terms=getTerm();
       User user=getUserAccount(userId);
       String debitAccounts=getUserWalletAddress(user);
       if(debitAccounts!=null){
       InformationFormResponseDTO informationFormResponseDTO=new InformationFormResponseDTO(debitAccounts,terms);
       return ResponseEntity.ok(informationFormResponseDTO);}
       return ResponseEntity.notFound().build();
   }

   @GetMapping("/account-status")
   public ResponseEntity<?> getAccuredInterest(@RequestBody String accountId) {
       SavingAccount savingAccount=savingAccountDAO.findById(accountId).orElse(null);
       if(savingAccount!=null){




           return ResponseEntity.ok().build();
       }
       return ResponseEntity.notFound().build();
   }


   @PostMapping("/add-saving-asset")
   public ResponseEntity<?> addUserSaving(@RequestParam(required = true) String userId,@RequestBody InformationFormPostRequestDTO entity) {
       //TODO: process POST request
       System.out.println(userId);
       userId="d00u86s5ig8jm25nu6q0";
       System.out.println(entity.toString());
       User user=getUserAccount(userId);
       DebitWallet account=debitWalletDAO.findByOneUserId(user.getId());    
       //Get selected Term instance
       Term selectedTerm=termDAO.findById(entity.getTermId()).orElse(null);
       if(selectedTerm==null){
            return ResponseEntity.badRequest().body("Term is not available please try again");
       }
       //Get provided OTP
       Integer OTP=provideOTP();

       //Check balance
       Boolean checkValidBalance=withdrawService.checkValidBalance(account, entity.getAmount());
       //Check OTP
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
           newSavingAccount.setInterestRate(selectedTerm.getInterestRate());
           // newSavingAccount.setCreatedBy(userId);
           // newSavingAccount.setCreatedDate(ZonedDateTime.now());
           newSavingAccount.setMaturityDate(null);
           newSavingAccount.setUser(user);
           newSavingAccount.setId(uuid.toString());
           // newSavingAccount.setUuid(uuid);
           newSavingAccount.setTerm(selectedTerm);
           savingAccountDAO.save(newSavingAccount);
           //Reduce balance
           withdrawService.TransferIntoSavingAccount(account, entity.getAmount());
           //Do onchain saving (WEB3)
            Boolean result=saveOnChain();
           //Response OK
           return ResponseEntity.ok("Successful");
       }
       return ResponseEntity.badRequest().body("Not succesful causing by server");
   }

   @PostMapping("/withdraw-saving")
   public ResponseEntity<?> withdrawSaving(@RequestBody String accountId) {
       //TODO: process POST request
       SavingAccount savingAccount=savingAccountDAO.findById(accountId).orElse(null);
       if(savingAccount!=null){
           Boolean userConfirmation=getUserConfirmation();
           if(userConfirmation){
               return ResponseEntity.ok().build();

           }
       }


       return ResponseEntity.badRequest().build();
   }

   private Boolean getUserConfirmation(){
       return null;
   }


   private Integer provideOTP(){
       return 123456;
   }

   private User getUserAccount(String userId){
       User user=userDAO.findById(userId).orElse(null);
       return user;
   }

   private DebitWallet getUserDebitWalletAddress(User user){
       return debitWalletDAO.findByOneUserId(user.getId());
   }
   private String getUserWalletAddress(User user){
       return user.getWalletAddress();
   }

   private List<Term> getTerm(){
       return termDAO.findByDeleted(false);
   }

   private Boolean saveOnChain(){
        Web3j web3j = Web3j.build(new HttpService("https://sepolia-rollup.arbitrum.io/rpc"));
        SavingAccountTest contract = SavingAccountTest.load(
            contractAddress,
            web3j,
            credentials,
            new DefaultGasProvider()
            );
        return false;

   }
}

