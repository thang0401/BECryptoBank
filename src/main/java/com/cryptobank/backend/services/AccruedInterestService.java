package com.cryptobank.backend.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.entity.Term;

@Service
public class AccruedInterestService {
    public Map<String,BigDecimal> calculateAccruedInterestOnSavingAccountDetailVer(SavingAccount account){
        Map<String,BigDecimal> detailSavingAccountAccruedInterest=new HashMap<>();


        BigDecimal accruedInterestPerFixedTerm=BigDecimal.ZERO;
        BigDecimal accruedInterestPerMonth=BigDecimal.ZERO;
        BigDecimal accruedInterestPerDay=BigDecimal.ZERO;


        OffsetDateTime startDate=account.getCreatedAt();
        Term accountTerm=account.getTerm();
        BigDecimal interestRate=account.getInterestRate();

        //Calculate maturity date
        OffsetDateTime maturityDate=startDate.plusMonths(accountTerm.getAmountMonth());
        // OffsetDateTime maturityDate=account.getMaturityDate();

        //Calculate
        accruedInterestPerFixedTerm= calculateAccruedInterestPerFixedTerm(account.getBalance(), interestRate,accountTerm.getAmountMonth());
        accruedInterestPerMonth= calculateAccruedInterestPerMonth(account.getBalance(), interestRate,accountTerm.getAmountMonth());
        accruedInterestPerDay= calculateAccruedInterestPerDay(account.getBalance(), interestRate,account.getCreatedAt(),maturityDate);

        //Put om map
        detailSavingAccountAccruedInterest.put("Fixed Term",accruedInterestPerFixedTerm);
        detailSavingAccountAccruedInterest.put("Per Month",accruedInterestPerMonth);
        detailSavingAccountAccruedInterest.put("Per Term",accruedInterestPerDay);


        return detailSavingAccountAccruedInterest;

    }

    private BigDecimal calculateAccruedInterestPerFixedTerm(BigDecimal balance,BigDecimal interestRate,Long months){
        BigDecimal result= balance.multiply(interestRate, MathContext.DECIMAL64);
        return result;
    }

    private BigDecimal calculateAccruedInterestPerMonth(BigDecimal balance,BigDecimal interestRate,Long months){
        OffsetDateTime currentMonth=OffsetDateTime.now();


        BigDecimal BigDecimalMonth=new BigDecimal(months);
        BigDecimal interestRatePerMonth=interestRate.divide(BigDecimalMonth);

        BigDecimal result= balance.multiply(interestRatePerMonth, MathContext.DECIMAL64);

        return result;
    }

    private BigDecimal calculateAccruedInterestPerDay(BigDecimal balance,BigDecimal interestRateOfTerm,OffsetDateTime from,OffsetDateTime to){
        Long days=ChronoUnit.DAYS.between(from, to);

        BigDecimal BigDecimalDay=new BigDecimal(days);
        BigDecimal interestRatePerDay=interestRateOfTerm.divide(BigDecimalDay);
        BigDecimal result= balance.multiply(interestRatePerDay, MathContext.DECIMAL64);
        return result;
    }

    

    private Boolean saveAccruedInterest(){  
        return null;
    }

    
}
