package com.cryptobank.backend.services;

import com.cryptobank.backend.smartcontract.CryptoBankDeposit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class Web3jService {
    private final Web3j web3j;
    private final Credentials credentials;
    private final CryptoBankDeposit contract;
    private final TransactionManager transactionManager;
    private final ContractGasProvider gasProvider;
    private final String cryptoBankWallet;
    private final String usdcTokenAddress;

    public Web3jService(
            @Value("${web3.provider.url}") String providerUrl,
            @Value("${web3.private.key}") String privateKey,
            @Value("${contract.address}") String contractAddress,
            @Value("${crypto.bank.wallet}") String cryptoBankWallet,
            @Value("${usdc.token.address}") String usdcTokenAddress) {
        try {
            this.web3j = Web3j.build(new HttpService(providerUrl));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to Web3 provider at " + providerUrl + ": " + e.getMessage(), e);
        }

        try {
            this.credentials = Credentials.create(privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid private key: " + e.getMessage(), e);
        }

        this.cryptoBankWallet = cryptoBankWallet;
        this.usdcTokenAddress = usdcTokenAddress;
        this.transactionManager = new RawTransactionManager(web3j, credentials);
        this.gasProvider = new DefaultGasProvider();

        BigInteger gasPrice = BigInteger.valueOf(1000000000L); // 1 Gwei
        BigInteger gasLimit = BigInteger.valueOf(500000L);
        StaticGasProvider gasProviderForContract = new StaticGasProvider(gasPrice, gasLimit);

        try {
            this.contract = new CryptoBankDeposit(contractAddress, web3j, credentials, gasProviderForContract);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize contract at address " + contractAddress + ": " + e.getMessage(), e);
        }

        System.out.println("Web3jService initialized with provider: " + providerUrl);
        System.out.println("Contract address: " + contractAddress);
        System.out.println("Owner address: " + credentials.getAddress());
        System.out.println("Crypto bank wallet address: " + cryptoBankWallet);
        System.out.println("USDC token address: " + usdcTokenAddress);
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
                    transactionHash
            ).send();

            System.out.println("Transaction successful. Hash: " + receipt.getTransactionHash());
            return receipt;
        } catch (Exception e) {
            System.err.println("Error signing transaction: " + e.getMessage());
            if (e instanceof TransactionException) {
                TransactionException txEx = (TransactionException) e;
                String revertReason = txEx.getTransactionReceipt()
                        .map(receipt -> receipt.getRevertReason())
                        .orElse("Unknown revert reason");
                throw new Exception("Deposit transaction failed: " + revertReason + " (Transaction Hash: " + txEx.getTransactionHash() + ")", e);
            }
            throw new Exception("Deposit transaction failed: " + e.getMessage(), e);
        }
    }

    public BigInteger getActualUsdcBalance(String walletAddress) throws Exception {
        Function function = new Function(
                "balanceOf",
                Collections.singletonList(new Address(walletAddress)),
                Collections.singletonList(new TypeReference<Uint256>() {})
        );
        String encodedFunction = FunctionEncoder.encode(function);

        Transaction transaction = Transaction.createEthCallTransaction(
                credentials.getAddress(),
                usdcTokenAddress,
                encodedFunction
        );

        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                transaction,
                DefaultBlockParameterName.LATEST
        ).send();

        if (response.hasError()) {
            String errorMessage = response.getError().getMessage();
            String errorData = response.getError().getData() != null ? response.getError().getData() : "No error data";
            throw new Exception("Failed to call balanceOf on USDC contract: " + errorMessage + " (Data: " + errorData + ")");
        }

        String responseValue = response.getValue();
        System.out.println("Response value from balanceOf: " + responseValue);

        if (responseValue == null || responseValue.equals("0x")) {
            throw new Exception("No data returned from balanceOf");
        }

        List<Type> results = FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        if (results.isEmpty()) {
            throw new Exception("Failed to decode response from balanceOf: No results returned");
        }

        return (BigInteger) results.get(0).getValue();
    }

    public TransactionReceipt signWithdrawTransaction(String fromPubKey, String toPubKey, String amount, String userId, String transactionHash) throws Exception {
        try {
            BigInteger amountInWei = new BigInteger(amount).multiply(BigInteger.valueOf(10).pow(6));

            // Kiểm tra số dư thực tế của ví tổng
            BigInteger actualBalance = getActualUsdcBalance(fromPubKey);
            System.out.println("Actual USDC balance in crypto bank wallet: " + actualBalance.toString() + " Wei");
            if (actualBalance.compareTo(amountInWei) < 0) {
                throw new IllegalStateException("Insufficient USDC balance in crypto bank wallet: " + actualBalance.toString() + " Wei");
            }

            System.out.println("Signing withdraw transaction:");
            System.out.println("UserId: " + userId);
            System.out.println("Amount (wei): " + amountInWei);
            System.out.println("From: " + fromPubKey);
            System.out.println("To: " + toPubKey);
            System.out.println("TransactionHash: " + transactionHash);

            // Tạo hàm transfer
            Function function = new Function(
                    "transfer",
                    Arrays.asList(new Address(toPubKey), new Uint256(amountInWei)),
                    Collections.singletonList(new TypeReference<Bool>() {})
            );
            String encodedFunction = FunctionEncoder.encode(function);

            // Ký và gửi giao dịch
            EthSendTransaction ethSendTransaction = transactionManager.sendTransaction(
                    gasProvider.getGasPrice(),
                    gasProvider.getGasLimit(),
                    usdcTokenAddress,
                    encodedFunction,
                    BigInteger.ZERO
            );

            if (ethSendTransaction.hasError()) {
                throw new Exception("Failed to send USDC transfer transaction: " + ethSendTransaction.getError().getMessage());
            }

            String transactionHashResult = ethSendTransaction.getTransactionHash();
            System.out.println("USDC Transfer Transaction Hash: " + transactionHashResult);

            // Chờ giao dịch được xác nhận bằng Web3j
            TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHashResult);
            if (!transactionReceipt.isStatusOK()) {
                throw new Exception("USDC transfer transaction failed: " + transactionReceipt.getStatus());
            }

            System.out.println("Withdraw transaction successful. Hash: " + transactionReceipt.getTransactionHash());
            return transactionReceipt;
        } catch (Exception e) {
            System.err.println("Error signing withdraw transaction: " + e.getMessage());
            throw new Exception("Withdraw transaction failed: " + e.getMessage(), e);
        }
    }

    private TransactionReceipt waitForTransactionReceipt(String transactionHash) throws Exception {
        int maxAttempts = 30; // Số lần thử tối đa
        int attempt = 0;
        long pollingInterval = 2000; // Thời gian chờ giữa các lần thử (2 giây)

        while (attempt < maxAttempts) {
            EthGetTransactionReceipt transactionReceiptResponse = web3j.ethGetTransactionReceipt(transactionHash).send();
            Optional<TransactionReceipt> transactionReceiptOptional = transactionReceiptResponse.getTransactionReceipt();

            if (transactionReceiptOptional.isPresent()) {
                return transactionReceiptOptional.get();
            }

            // Chờ một khoảng thời gian trước khi thử lại
            Thread.sleep(pollingInterval);
            attempt++;
        }

        throw new Exception("Transaction receipt not found after " + maxAttempts + " attempts for transaction hash: " + transactionHash);
    }
}