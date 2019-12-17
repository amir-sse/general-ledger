package com.leovegasgroup.generalledger.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leovegasgroup.generalledger.GeneralLedgerApplication;
import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.domain.Transaction;
import com.leovegasgroup.generalledger.domain.enumeration.Flag;
import com.leovegasgroup.generalledger.repository.CustomerAccountRepository;
import com.leovegasgroup.generalledger.repository.TransactionRepository;
import com.leovegasgroup.generalledger.service.dto.TransactionDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Integration tests for the {@Link TransactionResource} REST controller.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeneralLedgerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionResourceIT {

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(0);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(100);

    private static final Flag DEFAULT_IS_DEBIT = Flag.NO;

    private static final String DEFAULT_CUSTOMER_ID = "123456789";

    private static final String DEFAULT_ACCOUNT_TYPE = "AAAA";

    private static final Flag DEFAULT_HAS_NATURE_DEBIT = Flag.NO;


    private static final LocalDateTime DEFAULT_TIMESTAMP = LocalDateTime.now();

    private static final String DEFAULT_APP_TRANSACTION_ID = "BBBB";

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MockMvc restTransactionMockMvc;

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    private Transaction transaction;

    private TransactionDTO transactionDTO;

    private CustomerAccount customerAccount;

    ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void initTest() {
        this.customerAccount = createCustomerAccountEntity();
        this.customerAccount = this.customerAccountRepository.saveAndFlush(this.customerAccount);
        this.transaction = createTransactionEntity()
                .customerAccount(this.customerAccount);
        this.transactionDTO = createTransactionPostDTO()
                .customerAccountId(this.customerAccount.getId());
    }

    public static Transaction createTransactionEntity() {
        return new Transaction()
            .balance(DEFAULT_BALANCE)
            .amount(DEFAULT_AMOUNT)
            .isDebit(DEFAULT_IS_DEBIT)
            .timestamp(DEFAULT_TIMESTAMP)
            .appTransactionId(DEFAULT_APP_TRANSACTION_ID);
    }

    public static TransactionDTO createTransactionPostDTO() {
        return new TransactionDTO()
                .amount(DEFAULT_AMOUNT)
                .isDebit(DEFAULT_IS_DEBIT)
                .appTransactionId(DEFAULT_APP_TRANSACTION_ID);
    }

    public static CustomerAccount createCustomerAccountEntity() {
        return new CustomerAccount()
                .customerId(DEFAULT_CUSTOMER_ID)
                .accountType(DEFAULT_ACCOUNT_TYPE)
                .hasNatureDebit(DEFAULT_HAS_NATURE_DEBIT);
    }

    @Test
    @Transactional
    public void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writer().writeValueAsBytes(transactionDTO)))
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getBalance()).isEqualTo(DEFAULT_BALANCE);
        assertThat(testTransaction.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testTransaction.getIsDebit()).isEqualTo(DEFAULT_IS_DEBIT);
        assertThat(testTransaction.getAppTransactionId()).isEqualTo(DEFAULT_APP_TRANSACTION_ID);
    }


    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transactionDTO.setAmount(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writer().writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAppTransactionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transactionDTO.setAppTransactionId(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writer().writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc.perform(get("/api/transactions")
                .param("customer-account-id", customerAccount.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE.intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].isDebit").value(hasItem(DEFAULT_IS_DEBIT.toString())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].appTransactionId").value(hasItem(DEFAULT_APP_TRANSACTION_ID)));
    }
    
    @Test
    @Transactional
    public void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.balance").value(DEFAULT_BALANCE.intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.isDebit").value(DEFAULT_IS_DEBIT.toString()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.appTransactionId").value(DEFAULT_APP_TRANSACTION_ID));
    }

    @Test
    @Transactional
    public void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void equalsVerifier() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction();
        transaction2.setId(transaction1.getId());
        assertThat(transaction1).isEqualTo(transaction2);
        transaction2.setId(2L);
        assertThat(transaction1).isNotEqualTo(transaction2);
        transaction1.setId(null);
        assertThat(transaction1).isNotEqualTo(transaction2);
    }
}
