package com.cryptobank.backend.services;

import com.cryptobank.backend.smartcontract.CryptoBankDeposit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

@Service
public class Web3jService {
    private final Web3j web3j;
    private final Credentials credentials;
    private final CryptoBankDeposit contract;

    public Web3jService(
            @Value("${WEB3_PROVIDER_URL}") String providerUrl,
            @Value("${WEB3_PRIVATE_KEY}") String privateKey,
            @Value("${CONTRACT_ADDRESS1}") String contractAddress) {
        this.web3j = Web3j.build(new HttpService(providerUrl));
        this.credentials = Credentials.create(privateKey);

        // Cấu hình gas provider
        BigInteger gasPrice = BigInteger.valueOf(1000000000L); // 1 Gwei
        BigInteger gasLimit = BigInteger.valueOf(500000L); // Gas limit
        StaticGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

        // Khởi tạo smart contract
        this.contract = new CryptoBankDeposit(contractAddress, web3j, credentials, gasProvider);

        // Debug thông tin
        System.out.println("Web3jService initialized with provider: " + providerUrl);
        System.out.println("Contract address: " + contractAddress);
        System.out.println("Owner address: " + credentials.getAddress());
    }

    public TransactionReceipt signDepositTransaction(String fromPubKey, String toPubKey, String amount, String debitAccountId, String transactionHash) throws Exception {
        try {
            BigInteger amountInWei = new BigInteger(amount).multiply(BigInteger.valueOf(10).pow(6));
            System.out.println("Signing deposit transaction:");
            System.out.println("UserId: d0250rm199kgpknaiko0");
            System.out.println("DebitAccountId: " + debitAccountId);
            System.out.println("Amount (wei): " + amountInWei);
            System.out.println("TransactionHash: " + transactionHash);

            TransactionReceipt receipt = contract.deposit(
                    "d0250rm199kgpknaiko0",
                    debitAccountId,
                    amountInWei,
                    transactionHash // Sử dụng transactionHash từ payload
            ).send();

            System.out.println("Transaction successful. Hash: " + receipt.getTransactionHash());
            return receipt;
        } catch (Exception e) {
            System.err.println("Error signing transaction: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public TransactionReceipt createSaving(){
        
        return null;
    }
}