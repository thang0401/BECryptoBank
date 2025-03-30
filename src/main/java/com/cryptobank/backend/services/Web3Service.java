package com.cryptobank.backend.services;

import com.cryptobank.backend.entity.*;
import com.cryptobank.backend.repository.*;
import com.cryptobank.backend.smartcontract.USDCBank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;
import org.web3j.tx.TransactionManager;
import org.springframework.transaction.annotation.Transactional;
@Service
public class Web3Service {
    private final Web3j web3j;
    private final Credentials credentials;
    private final String contractAddress;
    private final RawTransactionManager transactionManager;
    private final ContractGasProvider gasProvider;
    private final TransactionManager txManager;

    public Web3Service(Web3j web3j) {
        this.web3j = web3j;
        this.credentials = Credentials.create("a35a31d78a0876d062615bc1da2f2b0dbfc37f8c229fc78c2fceff45904eb4e8"); // Use environment variables for security
        this.contractAddress = "0x9c3a3d7ea8e322b6630fd990b5e54bbc7242b36d";
        this.transactionManager = new RawTransactionManager(web3j, credentials);
        this.gasProvider = new DefaultGasProvider();
        this.txManager = new RawTransactionManager(web3j, credentials);
    }


}