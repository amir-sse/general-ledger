package com.leovegasgroup.generalledger.service;

import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.domain.Transaction;
import com.leovegasgroup.generalledger.repository.CustomerAccountRepository;
import com.leovegasgroup.generalledger.repository.TransactionRepository;
import com.leovegasgroup.generalledger.service.dto.TransactionDTO;
import com.leovegasgroup.generalledger.service.exception.DuplicateRequiredTransactionId;
import com.leovegasgroup.generalledger.service.exception.InsufficientBalanceException;
import com.leovegasgroup.generalledger.service.exception.RequiredTransactionIdMissed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService {
    private final Logger log = LoggerFactory.getLogger(TransactionService.class);


    private final TransactionRepository transactionRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final CustomerAccountService customerAccountService;

    public TransactionService(TransactionRepository transactionRepository, CustomerAccountRepository customerAccountRepository, CustomerAccountService customerAccountService) {
        this.transactionRepository = transactionRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.customerAccountService = customerAccountService;
    }


    /**
     * Validates the request
     * @param transactionDTO request
     */
    private void validate(TransactionDTO transactionDTO) {
        if  (transactionDTO.getAppTransactionId() == null) {
            throw new RequiredTransactionIdMissed();
        }
        if (transactionRepository.getFirstByAppTransactionId(transactionDTO.getAppTransactionId())
                .isPresent()) {
            throw new DuplicateRequiredTransactionId();
        }
        if (isThisAReductionTransaction(transactionDTO)
                && customerAccountService.calculateBalance(transactionDTO.getCustomerAccount())
                .subtract(transactionDTO.getAmount())
                .compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException();
        }
    }

    /**
     * Reads and locks the CustomerAccount and saves a new transaction according to the 'transactionDTO' request
     * @param transactionDTO request
     * @return issued transaction
     */
    @Transactional
    public Transaction issueTransaction(TransactionDTO transactionDTO) {
        validate(transactionDTO);

        // getting customerAccount with a pessimistic lock
        Optional<CustomerAccount> customerAccountOptional = customerAccountRepository.getById(transactionDTO.getCustomerAccount().getId());
        CustomerAccount lockedCustomerAccount = customerAccountOptional.orElseThrow(() -> {
            String message = String.format("'%d' customer-account not found", transactionDTO.getCustomerAccount().getId());
            log.warn(message);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        });
        BigDecimal balance = customerAccountService.calculateBalance(lockedCustomerAccount);
        if (isThisAReductionTransaction(transactionDTO)
                && balance.subtract(transactionDTO.getAmount())
                .compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException();
        }
        Transaction newTransaction = new Transaction()
                .amount(transactionDTO.getAmount())
                .balance(balance)
                .timestamp(LocalDateTime.now())
                .isDebit(transactionDTO.getIsDebit())
                .appTransactionId(transactionDTO.getAppTransactionId())
                .customerAccount(lockedCustomerAccount);
        newTransaction = transactionRepository.save(newTransaction);
        lockedCustomerAccount.setHeadTransaction(newTransaction);
        return newTransaction;
    }

    private boolean isThisAReductionTransaction(TransactionDTO transactionDTO) {
        return ! transactionDTO.getIsDebit().equals(transactionDTO.getCustomerAccount().getHasNatureDebit());
    }

}
