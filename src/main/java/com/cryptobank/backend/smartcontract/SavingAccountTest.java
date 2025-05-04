package com.cryptobank.backend.smartcontract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple12;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.2.
 */
@SuppressWarnings("rawtypes")
public class SavingAccountTest extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_ADDCONTRACT = "addContract";

    public static final String FUNC_CONTRACTS = "contracts";

    public static final String FUNC_CRYPTOBANKWALLET = "cryptoBankWallet";

    public static final String FUNC_DEBITBALANCES = "debitBalances";

    public static final String FUNC_DEPOSIT = "deposit";

    public static final String FUNC_DEPOSITS = "deposits";

    public static final String FUNC_GETCONTRACTCOUNT = "getContractCount";

    public static final String FUNC_GETCONTRACTSBYSAVINGACCOUNTID = "getContractsBySavingAccountId";

    public static final String FUNC_GETDEBITBALANCE = "getDebitBalance";

    public static final String FUNC_GETDEPOSIT = "getDeposit";

    public static final String FUNC_GETTOTALUSDCBALANCE = "getTotalUsdcBalance";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SAVINGBALANCE = "savingBalance";

    public static final String FUNC_TOTALTRANSACTIONFEE = "totalTransactionFee";

    public static final String FUNC_TOTALUSDCBALANCE = "totalUsdcBalance";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_UPDATECRYPTOBANKWALLET = "updateCryptoBankWallet";

    public static final String FUNC_UPDATEGGDRIVEURL = "updateGgDriveUrl";

    public static final String FUNC_UPDATEHEIRSTATUS = "updateHeirStatus";

    public static final String FUNC_UPDATEOWNERID = "updateOwnerId";

    public static final String FUNC_USDCTOKEN = "usdcToken";

    public static final String FUNC_WITHDRAWUSDC = "withdrawUsdc";

    public static final Event CONTRACTADDED_EVENT = new Event("ContractAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event CRYPTOBANKWALLETUPDATED_EVENT = new Event("CryptoBankwalletUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event DEPOSIT_EVENT = new Event("Deposit", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event GGDRIVEURLUPDATED_EVENT = new Event("GgDriveUrlUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event HEIRSTATUSUPDATED_EVENT = new Event("HeirStatusUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event OWNERIDUPDATED_EVENT = new Event("OwnerIdUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event WITHDRAW_EVENT = new Event("Withdraw", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected SavingAccountTest(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SavingAccountTest(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SavingAccountTest(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SavingAccountTest(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<ContractAddedEventResponse> getContractAddedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CONTRACTADDED_EVENT, transactionReceipt);
        ArrayList<ContractAddedEventResponse> responses = new ArrayList<ContractAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ContractAddedEventResponse typedResponse = new ContractAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.contractId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.savingAccountId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ContractAddedEventResponse getContractAddedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CONTRACTADDED_EVENT, log);
        ContractAddedEventResponse typedResponse = new ContractAddedEventResponse();
        typedResponse.log = log;
        typedResponse.contractId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.savingAccountId = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ContractAddedEventResponse> contractAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getContractAddedEventFromLog(log));
    }

    public Flowable<ContractAddedEventResponse> contractAddedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CONTRACTADDED_EVENT));
        return contractAddedEventFlowable(filter);
    }

    public static List<CryptoBankwalletUpdatedEventResponse> getCryptoBankwalletUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CRYPTOBANKWALLETUPDATED_EVENT, transactionReceipt);
        ArrayList<CryptoBankwalletUpdatedEventResponse> responses = new ArrayList<CryptoBankwalletUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CryptoBankwalletUpdatedEventResponse typedResponse = new CryptoBankwalletUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldWallet = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newWallet = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CryptoBankwalletUpdatedEventResponse getCryptoBankwalletUpdatedEventFromLog(
            Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CRYPTOBANKWALLETUPDATED_EVENT, log);
        CryptoBankwalletUpdatedEventResponse typedResponse = new CryptoBankwalletUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.oldWallet = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newWallet = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<CryptoBankwalletUpdatedEventResponse> cryptoBankwalletUpdatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCryptoBankwalletUpdatedEventFromLog(log));
    }

    public Flowable<CryptoBankwalletUpdatedEventResponse> cryptoBankwalletUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CRYPTOBANKWALLETUPDATED_EVENT));
        return cryptoBankwalletUpdatedEventFlowable(filter);
    }

    public static List<DepositEventResponse> getDepositEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DEPOSIT_EVENT, transactionReceipt);
        ArrayList<DepositEventResponse> responses = new ArrayList<DepositEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositEventResponse typedResponse = new DepositEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.userId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.debitAccountId = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.transactionHash = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DepositEventResponse getDepositEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DEPOSIT_EVENT, log);
        DepositEventResponse typedResponse = new DepositEventResponse();
        typedResponse.log = log;
        typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.userId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.debitAccountId = (byte[]) eventValues.getIndexedValues().get(2).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.transactionHash = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<DepositEventResponse> depositEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDepositEventFromLog(log));
    }

    public Flowable<DepositEventResponse> depositEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSIT_EVENT));
        return depositEventFlowable(filter);
    }

    public static List<GgDriveUrlUpdatedEventResponse> getGgDriveUrlUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(GGDRIVEURLUPDATED_EVENT, transactionReceipt);
        ArrayList<GgDriveUrlUpdatedEventResponse> responses = new ArrayList<GgDriveUrlUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            GgDriveUrlUpdatedEventResponse typedResponse = new GgDriveUrlUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.contractId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.savingAccountId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newGgDriveUrl = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static GgDriveUrlUpdatedEventResponse getGgDriveUrlUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(GGDRIVEURLUPDATED_EVENT, log);
        GgDriveUrlUpdatedEventResponse typedResponse = new GgDriveUrlUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.contractId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.savingAccountId = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newGgDriveUrl = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<GgDriveUrlUpdatedEventResponse> ggDriveUrlUpdatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getGgDriveUrlUpdatedEventFromLog(log));
    }

    public Flowable<GgDriveUrlUpdatedEventResponse> ggDriveUrlUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(GGDRIVEURLUPDATED_EVENT));
        return ggDriveUrlUpdatedEventFlowable(filter);
    }

    public static List<HeirStatusUpdatedEventResponse> getHeirStatusUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(HEIRSTATUSUPDATED_EVENT, transactionReceipt);
        ArrayList<HeirStatusUpdatedEventResponse> responses = new ArrayList<HeirStatusUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            HeirStatusUpdatedEventResponse typedResponse = new HeirStatusUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.contractId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.savingAccountId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newHeirStatus = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static HeirStatusUpdatedEventResponse getHeirStatusUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(HEIRSTATUSUPDATED_EVENT, log);
        HeirStatusUpdatedEventResponse typedResponse = new HeirStatusUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.contractId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.savingAccountId = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newHeirStatus = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<HeirStatusUpdatedEventResponse> heirStatusUpdatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getHeirStatusUpdatedEventFromLog(log));
    }

    public Flowable<HeirStatusUpdatedEventResponse> heirStatusUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(HEIRSTATUSUPDATED_EVENT));
        return heirStatusUpdatedEventFlowable(filter);
    }

    public static List<OwnerIdUpdatedEventResponse> getOwnerIdUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERIDUPDATED_EVENT, transactionReceipt);
        ArrayList<OwnerIdUpdatedEventResponse> responses = new ArrayList<OwnerIdUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnerIdUpdatedEventResponse typedResponse = new OwnerIdUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.contractId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.savingAccountId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newOwnerId = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnerIdUpdatedEventResponse getOwnerIdUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERIDUPDATED_EVENT, log);
        OwnerIdUpdatedEventResponse typedResponse = new OwnerIdUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.contractId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.savingAccountId = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newOwnerId = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnerIdUpdatedEventResponse> ownerIdUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnerIdUpdatedEventFromLog(log));
    }

    public Flowable<OwnerIdUpdatedEventResponse> ownerIdUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERIDUPDATED_EVENT));
        return ownerIdUpdatedEventFlowable(filter);
    }

    public static List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnershipTransferredEventResponse getOwnershipTransferredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
        OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
        typedResponse.log = log;
        typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnershipTransferredEventFromLog(log));
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public static List<WithdrawEventResponse> getWithdrawEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(WITHDRAW_EVENT, transactionReceipt);
        ArrayList<WithdrawEventResponse> responses = new ArrayList<WithdrawEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            WithdrawEventResponse typedResponse = new WithdrawEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.userId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.transactionHash = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static WithdrawEventResponse getWithdrawEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(WITHDRAW_EVENT, log);
        WithdrawEventResponse typedResponse = new WithdrawEventResponse();
        typedResponse.log = log;
        typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.userId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.transactionHash = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<WithdrawEventResponse> withdrawEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getWithdrawEventFromLog(log));
    }

    public Flowable<WithdrawEventResponse> withdrawEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(WITHDRAW_EVENT));
        return withdrawEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addContract(ContractInput input) {
        final Function function = new Function(
                FUNC_ADDCONTRACT, 
                Arrays.<Type>asList(input), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple12<String, String, BigInteger, BigInteger, String, String, String, String, String, String, String, String>> contracts(
            BigInteger param0) {
        final Function function = new Function(FUNC_CONTRACTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteFunctionCall<Tuple12<String, String, BigInteger, BigInteger, String, String, String, String, String, String, String, String>>(function,
                new Callable<Tuple12<String, String, BigInteger, BigInteger, String, String, String, String, String, String, String, String>>() {
                    @Override
                    public Tuple12<String, String, BigInteger, BigInteger, String, String, String, String, String, String, String, String> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple12<String, String, BigInteger, BigInteger, String, String, String, String, String, String, String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (String) results.get(5).getValue(), 
                                (String) results.get(6).getValue(), 
                                (String) results.get(7).getValue(), 
                                (String) results.get(8).getValue(), 
                                (String) results.get(9).getValue(), 
                                (String) results.get(10).getValue(), 
                                (String) results.get(11).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> cryptoBankWallet() {
        final Function function = new Function(FUNC_CRYPTOBANKWALLET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> debitBalances(String param0) {
        final Function function = new Function(FUNC_DEBITBALANCES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> deposit(String userId, String debitAccountId,
            BigInteger amount, String transactionHash) {
        final Function function = new Function(
                FUNC_DEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(userId), 
                new org.web3j.abi.datatypes.Utf8String(debitAccountId), 
                new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.Utf8String(transactionHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple7<String, String, String, BigInteger, String, BigInteger, Boolean>> deposits(
            String param0) {
        final Function function = new Function(FUNC_DEPOSITS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple7<String, String, String, BigInteger, String, BigInteger, Boolean>>(function,
                new Callable<Tuple7<String, String, String, BigInteger, String, BigInteger, Boolean>>() {
                    @Override
                    public Tuple7<String, String, String, BigInteger, String, BigInteger, Boolean> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<String, String, String, BigInteger, String, BigInteger, Boolean>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (Boolean) results.get(6).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getContractCount() {
        final Function function = new Function(FUNC_GETCONTRACTCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getContractsBySavingAccountId(String _savingAccountId) {
        final Function function = new Function(FUNC_GETCONTRACTSBYSAVINGACCOUNTID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_savingAccountId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<ContractInput>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getDebitBalance(String userId) {
        final Function function = new Function(FUNC_GETDEBITBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(userId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple5<String, String, String, BigInteger, BigInteger>> getDeposit(
            String transactionHash) {
        final Function function = new Function(FUNC_GETDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(transactionHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple5<String, String, String, BigInteger, BigInteger>>(function,
                new Callable<Tuple5<String, String, String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple5<String, String, String, BigInteger, BigInteger> call() throws
                            Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<String, String, String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getTotalUsdcBalance() {
        final Function function = new Function(FUNC_GETTOTALUSDCBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> savingBalance() {
        final Function function = new Function(FUNC_SAVINGBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> totalTransactionFee() {
        final Function function = new Function(FUNC_TOTALTRANSACTIONFEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> totalUsdcBalance() {
        final Function function = new Function(FUNC_TOTALUSDCBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateCryptoBankWallet(String newWallet) {
        final Function function = new Function(
                FUNC_UPDATECRYPTOBANKWALLET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newWallet)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateGgDriveUrl(BigInteger contractId,
            String _savingAccountId, String newGgDriveUrl) {
        final Function function = new Function(
                FUNC_UPDATEGGDRIVEURL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(contractId), 
                new org.web3j.abi.datatypes.Utf8String(_savingAccountId), 
                new org.web3j.abi.datatypes.Utf8String(newGgDriveUrl)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateHeirStatus(BigInteger contractId,
            String _savingAccountId, String newHeirStatus) {
        final Function function = new Function(
                FUNC_UPDATEHEIRSTATUS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(contractId), 
                new org.web3j.abi.datatypes.Utf8String(_savingAccountId), 
                new org.web3j.abi.datatypes.Utf8String(newHeirStatus)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateOwnerId(BigInteger contractId,
            String _savingAccountId, String newOwnerId) {
        final Function function = new Function(
                FUNC_UPDATEOWNERID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(contractId), 
                new org.web3j.abi.datatypes.Utf8String(_savingAccountId), 
                new org.web3j.abi.datatypes.Utf8String(newOwnerId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> usdcToken() {
        final Function function = new Function(FUNC_USDCTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawUsdc(String to, String userId,
            BigInteger amount, String transactionHash) {
        final Function function = new Function(
                FUNC_WITHDRAWUSDC, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.Utf8String(userId), 
                new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.Utf8String(transactionHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static SavingAccountTest load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SavingAccountTest(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SavingAccountTest load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SavingAccountTest(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SavingAccountTest load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SavingAccountTest(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SavingAccountTest load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SavingAccountTest(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class ContractInput extends DynamicStruct {
        public String savingAccountId;

        public String term;

        public BigInteger startDate;

        public BigInteger endDate;

        public String supportStaff;

        public String ggDriveUrl;

        public String ownerName;

        public String ownerId;

        public String email;

        public String phone;

        public String status;

        public String heirStatus;

        public ContractInput(String savingAccountId, String term, BigInteger startDate,
                BigInteger endDate, String supportStaff, String ggDriveUrl, String ownerName,
                String ownerId, String email, String phone, String status, String heirStatus) {
            super(new org.web3j.abi.datatypes.Utf8String(savingAccountId), 
                    new org.web3j.abi.datatypes.Utf8String(term), 
                    new org.web3j.abi.datatypes.generated.Uint256(startDate), 
                    new org.web3j.abi.datatypes.generated.Uint256(endDate), 
                    new org.web3j.abi.datatypes.Utf8String(supportStaff), 
                    new org.web3j.abi.datatypes.Utf8String(ggDriveUrl), 
                    new org.web3j.abi.datatypes.Utf8String(ownerName), 
                    new org.web3j.abi.datatypes.Utf8String(ownerId), 
                    new org.web3j.abi.datatypes.Utf8String(email), 
                    new org.web3j.abi.datatypes.Utf8String(phone), 
                    new org.web3j.abi.datatypes.Utf8String(status), 
                    new org.web3j.abi.datatypes.Utf8String(heirStatus));
            this.savingAccountId = savingAccountId;
            this.term = term;
            this.startDate = startDate;
            this.endDate = endDate;
            this.supportStaff = supportStaff;
            this.ggDriveUrl = ggDriveUrl;
            this.ownerName = ownerName;
            this.ownerId = ownerId;
            this.email = email;
            this.phone = phone;
            this.status = status;
            this.heirStatus = heirStatus;
        }

        public ContractInput(Utf8String savingAccountId, Utf8String term, Uint256 startDate,
                Uint256 endDate, Utf8String supportStaff, Utf8String ggDriveUrl,
                Utf8String ownerName, Utf8String ownerId, Utf8String email, Utf8String phone,
                Utf8String status, Utf8String heirStatus) {
            super(savingAccountId, term, startDate, endDate, supportStaff, ggDriveUrl, ownerName, ownerId, email, phone, status, heirStatus);
            this.savingAccountId = savingAccountId.getValue();
            this.term = term.getValue();
            this.startDate = startDate.getValue();
            this.endDate = endDate.getValue();
            this.supportStaff = supportStaff.getValue();
            this.ggDriveUrl = ggDriveUrl.getValue();
            this.ownerName = ownerName.getValue();
            this.ownerId = ownerId.getValue();
            this.email = email.getValue();
            this.phone = phone.getValue();
            this.status = status.getValue();
            this.heirStatus = heirStatus.getValue();
        }
    }

    public static class ContractAddedEventResponse extends BaseEventResponse {
        public BigInteger contractId;

        public String savingAccountId;
    }

    public static class CryptoBankwalletUpdatedEventResponse extends BaseEventResponse {
        public String oldWallet;

        public String newWallet;
    }

    public static class DepositEventResponse extends BaseEventResponse {
        public String user;

        public byte[] userId;

        public byte[] debitAccountId;

        public BigInteger amount;

        public String transactionHash;

        public BigInteger timestamp;
    }

    public static class GgDriveUrlUpdatedEventResponse extends BaseEventResponse {
        public BigInteger contractId;

        public String savingAccountId;

        public String newGgDriveUrl;
    }

    public static class HeirStatusUpdatedEventResponse extends BaseEventResponse {
        public BigInteger contractId;

        public String savingAccountId;

        public String newHeirStatus;
    }

    public static class OwnerIdUpdatedEventResponse extends BaseEventResponse {
        public BigInteger contractId;

        public String savingAccountId;

        public String newOwnerId;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class WithdrawEventResponse extends BaseEventResponse {
        public String user;

        public byte[] userId;

        public BigInteger amount;

        public String transactionHash;

        public BigInteger timestamp;
    }
}
