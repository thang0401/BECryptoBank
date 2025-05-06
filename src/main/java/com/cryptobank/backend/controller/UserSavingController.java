package com.cryptobank.backend.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.cryptobank.backend.DTO.SavingHeirFormDTO.AddHeirFormDTO;
import com.cryptobank.backend.DTO.UserSavingAccountDTO.InformationFormPostRequestDTO;
import com.cryptobank.backend.DTO.UserSavingAccountDTO.InformationFormResponseDTO;
import com.cryptobank.backend.DTO.UserSavingAccountDTO.UserSavingGetAllResponse;
import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.Term;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.SavingAccountDAO;
import com.cryptobank.backend.repository.StatusDAO;
import com.cryptobank.backend.repository.TermDAO;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.services.WithdrawService;
import com.cryptobank.backend.smartcontract.SavingAccountTest;

import jakarta.transaction.Transactional;

import com.cryptobank.backend.services.AccruedInterestService;
import com.cryptobank.backend.services.SavingAccountService;
import com.cryptobank.backend.services.Web3jService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/user/saving")
public class UserSavingController {
   @Autowired
   TermDAO termDAO;
   UserDAO userDAO;
   SavingAccountDAO savingAccountDAO;
   DebitWalletDAO debitWalletDAO;
   WithdrawService withdrawService;
   AccruedInterestService accruedInterestService;
   Web3jService web3jService;
    StatusDAO statusDAO;
    SavingAccountService SVService;

   @GetMapping("/add-saving-asset")
   public ResponseEntity<InformationFormResponseDTO> getData(@RequestParam String userId) {
       List<Term> terms=getTerm();
       User user=getUserAccount(userId);
       DebitWallet debitAccounts=debitWalletDAO.findByOneUserId(userId);
       if(debitAccounts!=null){
       InformationFormResponseDTO informationFormResponseDTO=new InformationFormResponseDTO(debitAccounts,terms,user.getEmail());
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
       System.out.println(entity.toString());
       User user=getUserAccount(userId);
       DebitWallet account=debitWalletDAO.findByOneUserId(user.getId());    
       //Get selected Term instance
       Term selectedTerm=termDAO.findById(entity.getTermId()).orElse(null);
       if(selectedTerm==null){
            return ResponseEntity.badRequest().body("Term is not available please try again");
       }
       //Check balance
       Boolean checkValidBalance=withdrawService.checkValidBalance(account, entity.getAmount());
       //Check valid balance
       if(!checkValidBalance){
           return ResponseEntity.badRequest().body("Not succesful causing by insufficient balance");
       }

       if(checkValidBalance){
           UUID uuid=UUID.randomUUID();
           Status status=statusDAO.findById("cvvvf5bme6nnaun2s4dg").orElse(null);
           if(status==null){
            System.out.println("Status not found and status will be set to null");
           }
           //Save to DB
           SavingAccount newSavingAccount=new SavingAccount();
           newSavingAccount.setBalance(entity.getAmount());
           // newSavingAccount.setHeirStatus(false);
           newSavingAccount.setInterestRate(selectedTerm.getInterestRate());
           // newSavingAccount.setCreatedBy(userId);
           // newSavingAccount.setCreatedDate(ZonedDateTime.now());
           OffsetDateTime maturityDate = OffsetDateTime.now(ZoneOffset.UTC).plusMonths(selectedTerm.getAmountMonth());
           newSavingAccount.setMaturityDate(maturityDate);
           newSavingAccount.setUser(user);
           newSavingAccount.setId(uuid.toString());
           newSavingAccount.setStatus(status);
           newSavingAccount.setDeleted(false);
           // newSavingAccount.setUuid(uuid);
           newSavingAccount.setTerm(selectedTerm);
           savingAccountDAO.save(newSavingAccount);
           //Reduce balance
           Boolean withdrawSuccessful=withdrawService.TransferIntoSavingAccount(account, entity.getAmount());
           if(withdrawSuccessful==false){
            return ResponseEntity.badRequest().body("Not succesful withdrawn issue");
           }
           System.out.println(withdrawSuccessful);
           //Do onchain saving (WEB3)
           //Response OK
           return ResponseEntity.ok("Successful");
       }
       return ResponseEntity.badRequest().body("Not succesful causing by server");
   }

   @Transactional
   @PostMapping("/withdraw-saving")
   public ResponseEntity<?> withdrawSaving(@RequestBody String accountId) {
        System.out.println(accountId);
       SavingAccount savingAccount=savingAccountDAO.findById(accountId).orElse(null);
       System.out.println(savingAccount);
       if(savingAccount!=null){
            savingAccount.setDeleted(true);
            DebitWallet userWallet=savingAccount.getUser().getDebitWalletList();
            userWallet.setBalance(userWallet.getBalance().add(savingAccount.getBalance()));
            debitWalletDAO.save(userWallet);
            savingAccountDAO.save(savingAccount);
            //   Get accrued balance if not null will be transfer to bank
            return ResponseEntity.ok().build();
       }
       return ResponseEntity.notFound().build();
   }

   @PostMapping("/add-heir")
   public ResponseEntity<?> addAHeir(@RequestBody AddHeirFormDTO addHeirFormDTO) {
        SavingAccount savingAccount=savingAccountDAO.findById(addHeirFormDTO.getSavingAccountId()).orElse(null);
        if( savingAccount!=null){
            savingAccount.setHeirName(addHeirFormDTO.getHeirId());
            savingAccount.setGgDriveUrl(addHeirFormDTO.getGgDriveLink());
            savingAccount.setHeirStatus(true);
            //Web 3 implementation
            String transactionHash="";//Get from WEB3 implementation
            //
            savingAccount.setTransactionHash(transactionHash);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
   }

   @PostMapping("/remove-heir")
   public ResponseEntity<?> removeHeir(@RequestBody String savingAccountId) {
       //TODO: process POST request
       SavingAccount savingAccount=savingAccountDAO.findById(savingAccountId).orElse(null);
        if( savingAccount!=null){
            savingAccount.setHeirName("");
            savingAccount.setGgDriveUrl("");
            savingAccount.setHeirStatus(false);
            //Web 3 implementation
            String transactionHash="";//Get from WEB3 implementation
            //
            savingAccount.setTransactionHash(transactionHash);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
   }

   @GetMapping("/get-savings")
   public ResponseEntity<?> getUserSavings(@RequestParam String userId) {
    List<SavingAccount> userPortfolios = SVService.getUserPortfoliosByCustomerId(userId);
        if(userPortfolios!=null){
            List<UserSavingGetAllResponse> responses=new ArrayList<>();
            for(SavingAccount sv:userPortfolios){
                if(!sv.getDeleted()){
                UserSavingGetAllResponse res=new UserSavingGetAllResponse();
                res.setAccountId(sv.getId());
                res.setBalance(sv.getBalance());
                res.setEndDate(sv.getMaturityDate().toString());
                res.setStartDate(sv.getCreatedAt().toString());
                res.setIsHeir(sv.getHeirStatus());
                res.setTerm(sv.getTerm().getAmountMonth());
                res.setUserEmail(sv.getUser().getEmail());
                res.setUserId(sv.getUser().getId());
                res.setUserPhone(sv.getUser().getPhoneNumber());
                res.setUserName(sv.getUser().getFullName());
                res.setStatus(sv.getStatus().getName());
                responses.add(res);
            }
            }
            return ResponseEntity.ok(responses);
        }
       return ResponseEntity.notFound().build();
   }
   
   @GetMapping("/get-saving")
   public ResponseEntity<?> getMethodName(@RequestParam String id) {
        SavingAccount sv=savingAccountDAO.findById(id).orElse(null);
        if(sv!=null){
            UserSavingGetAllResponse res=new UserSavingGetAllResponse();
                res.setAccountId(sv.getId());
                res.setBalance(sv.getBalance());
                res.setEndDate(sv.getMaturityDate().toString());
                res.setStartDate(sv.getCreatedAt().toString());
                res.setIsHeir(sv.getHeirStatus());
                res.setTerm(sv.getTerm().getAmountMonth());
                res.setUserEmail(sv.getUser().getEmail());
                res.setUserId(sv.getUser().getId());
                res.setUserPhone(sv.getUser().getPhoneNumber());
                res.setUserName(sv.getUser().getFullName());
                res.setStatus(sv.getStatus().getName());
            return ResponseEntity.ok().body(res);
        }
       return ResponseEntity.notFound().build();
   }
   

   private Boolean getUserConfirmation(){
       return null;
   }


 

   private User getUserAccount(String userId){
       User user=userDAO.findById(userId).orElse(null);
       return user;
   }

  

   private List<Term> getTerm(){
       return termDAO.findByDeleted(false);
   }

}

