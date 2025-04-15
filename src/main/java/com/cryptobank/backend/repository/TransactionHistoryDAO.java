package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionHistoryDAO extends JpaRepository<TransactionHistory, String> {
//
//    @Query("select d.id, d.sentPublicKey as sent, d.receivedPortfolio as received, d.createdDate as createdDate, 'Deposit' as type from DepositPortfolioHistory d union all " +
//            "select w.id, w.sentPortfolio.id as sent, w.receivedPublicKey as received, w.createdDate as createdDate, 'Withdraw' as type from WithdrawPortfolioHistory w union all " +
//            "select t.id, t.sentPortfolio.id as sent, t.receivedPortfolio as received, t.createdDate as createdDate, 'Transfer' as type from TransferPortfolioHistory t " +
//            "order by createdDate")
//    List<TransactionHistory> getAllTransactionHistory();
//
//    @Query("select d.id, d.receivedPortfolio.user as user, d.sentPublicKey as sent, d.receivedPortfolio as received, d.createdDate as createdDate, 'Deposit' as type from DepositPortfolioHistory d union all " +
//            "select w.id, w.sentPortfolio.user as user, w.sentPortfolio.id as sent, w.receivedPublicKey as received, w.createdDate as createdDate, 'Withdraw' as type from WithdrawPortfolioHistory w union all " +
//            "select t1.id, t1.sentPortfolio.user as user, t1.sentPortfolio.id as sent, t1.receivedPortfolio as received, t1.createdDate as createdDate, 'TransferSend' as type from TransferPortfolioHistory t1 union all " +
//            "select t2.id, t2.receivedPortfolio.user as user, t2.sentPortfolio.id as sent, t2.receivedPortfolio as received, t2.createdDate as createdDate, 'TransferReceive' as type from TransferPortfolioHistory t2 " +
//            "where user.id = :userId " +
//            "order by createdDate")
//    List<TransactionHistory> getAllTransactionHistory(String userId);

}
