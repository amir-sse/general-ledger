package com.leovegasgroup.generalledger.service;


import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.domain.Transaction;
import com.leovegasgroup.generalledger.domain.enumeration.Flag;
import com.leovegasgroup.generalledger.repository.CustomerAccountRepository;
import com.leovegasgroup.generalledger.repository.TransactionRepository;
import com.leovegasgroup.generalledger.service.dto.TransactionDTO;
import com.leovegasgroup.generalledger.service.exception.DuplicateRequiredTransactionId;
import com.leovegasgroup.generalledger.service.exception.InsufficientBalanceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @Mock
    CustomerAccountRepository customerAccountRepository;

    @Mock
    TransactionRepository transactionRepository;

    private CustomerAccountService customerAccountService;
    private TransactionService transactionService;
    private CustomerAccount defaultCustomerAccount;
    private Transaction initTransaction;
    private TransactionDTO debitTransactionDTO;
    private TransactionDTO creditTransactionDTO;
    private Transaction savedTransaction;

    private BigDecimal initBalance = BigDecimal.valueOf(240);
    private String initAppTransactionId = "100000";
    private String debitAppTransactionId = "100001";
    private String creditAppTransactionId = "100002";

    @Before
    public void init() {
        defaultCustomerAccount = new CustomerAccount()
                .hasNatureDebit(Flag.YES);
        initTransaction = new Transaction()
                .appTransactionId(initAppTransactionId)
                .amount(initBalance)
                .balance(BigDecimal.valueOf(0))
                .isDebit(Flag.YES);
        initTransaction.setCustomerAccount(defaultCustomerAccount);
        defaultCustomerAccount.setHeadTransaction(initTransaction);

        debitTransactionDTO = new TransactionDTO()
                .appTransactionId(debitAppTransactionId)
                .amount(BigDecimal.valueOf(100))
                .isDebit(Flag.YES);
        debitTransactionDTO.setCustomerAccount(defaultCustomerAccount);

        creditTransactionDTO = new TransactionDTO()
                .appTransactionId(creditAppTransactionId)
                .amount(BigDecimal.valueOf(100))
                .isDebit(Flag.NO);
        creditTransactionDTO.setCustomerAccount(defaultCustomerAccount);

        savedTransaction = new Transaction()
                .amount(BigDecimal.valueOf(100));

        customerAccountService = new CustomerAccountService(customerAccountRepository);
        transactionService = new TransactionService(transactionRepository, customerAccountRepository, customerAccountService);
    }

    @Test
    public void calculateBalance() {
        BigDecimal calculateBalance = customerAccountService.calculateBalance(defaultCustomerAccount);
        assertEquals(initBalance, calculateBalance);
    }

    @Test
    public void issueTransaction_debit100credit100MockedDebitAccount_balanceShouldNotChange() {
        when(customerAccountRepository.getById(any())).thenReturn(Optional.of(defaultCustomerAccount));

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        transactionService.issueTransaction(creditTransactionDTO);
        verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());
        savedTransaction.setBalance(transactionArgumentCaptor.getValue().getBalance());
        savedTransaction.setIsDebit(transactionArgumentCaptor.getValue().getIsDebit());

        transactionService.issueTransaction(debitTransactionDTO);
        verify(transactionRepository, times(2)).save(transactionArgumentCaptor.capture());
        savedTransaction.setBalance(transactionArgumentCaptor.getValue().getBalance());
        savedTransaction.setIsDebit(transactionArgumentCaptor.getValue().getIsDebit());

        assertEquals(initBalance, customerAccountService.calculateBalance(defaultCustomerAccount));
    }

    @Test(expected = InsufficientBalanceException.class)
    public void issueTransaction_credit100threeTimesMockedDebitAccount_theLastTransactionShouldFail() {
        when(customerAccountRepository.getById(any())).thenReturn(Optional.of(defaultCustomerAccount));

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        transactionService.issueTransaction(creditTransactionDTO);
        verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());
        savedTransaction.setBalance(transactionArgumentCaptor.getValue().getBalance());
        savedTransaction.setIsDebit(transactionArgumentCaptor.getValue().getIsDebit());

        transactionService.issueTransaction(creditTransactionDTO);
        verify(transactionRepository, times(2)).save(transactionArgumentCaptor.capture());
        savedTransaction.setBalance(transactionArgumentCaptor.getValue().getBalance());
        savedTransaction.setIsDebit(transactionArgumentCaptor.getValue().getIsDebit());

        assertEquals(initBalance.subtract(BigDecimal.valueOf(200)), customerAccountService.calculateBalance(defaultCustomerAccount));

        transactionService.issueTransaction(creditTransactionDTO);
    }

    @Test(expected = DuplicateRequiredTransactionId.class)
    public void issueTransaction_WithUsedAppTransactionId_shouldFail() {
        savedTransaction.setAppTransactionId(debitAppTransactionId);
        Optional<Transaction> foundTransactionWithUsedAppTransactionId = Optional.of(savedTransaction);

        when(transactionRepository.getFirstByAppTransactionId(any())).thenReturn(foundTransactionWithUsedAppTransactionId);

        transactionService.issueTransaction(debitTransactionDTO);
    }

}
