package com.leovegasgroup.generalledger.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leovegasgroup.generalledger.GeneralLedgerApplication;
import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.domain.enumeration.Flag;
import com.leovegasgroup.generalledger.repository.CustomerAccountRepository;
import com.leovegasgroup.generalledger.service.dto.CustomerAccountDTO;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Integration tests for the {@Link CustomerAccountResource} REST controller.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeneralLedgerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerAccountResourceIT {

    private static final String DEFAULT_CUSTOMER_ID = "123456789";

    private static final String DEFAULT_ACCOUNT_TYPE = "AAAA";

    private static final Flag DEFAULT_HAS_NATURE_DEBIT = Flag.NO;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    @Autowired
    private MockMvc restCustomerAccountMockMvc;

    private CustomerAccountDTO customerAccountDTO;

    private CustomerAccount customerAccount;

    public static CustomerAccountDTO createPostDTO() {
        return new CustomerAccountDTO()
            .customerId(DEFAULT_CUSTOMER_ID)
            .accountType(DEFAULT_ACCOUNT_TYPE)
            .hasNatureDebit(DEFAULT_HAS_NATURE_DEBIT);
    }

    public static CustomerAccount createEntity() {
        return new CustomerAccount()
                .customerId(DEFAULT_CUSTOMER_ID)
                .accountType(DEFAULT_ACCOUNT_TYPE)
                .hasNatureDebit(DEFAULT_HAS_NATURE_DEBIT);
    }

    @Before
    public void initTest() {
        this.customerAccountDTO = createPostDTO();
        this.customerAccount = createEntity();
    }

    @Test
    @Transactional
    public void createCustomerAccount() throws Exception {
        int databaseSizeBeforeCreate = customerAccountRepository.findAll().size();

        // Create the CustomerAccount
        restCustomerAccountMockMvc.perform(post("/api/customer-accounts")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writer().writeValueAsBytes(customerAccountDTO)))
            .andExpect(status().isCreated());

        // Validate the CustomerAccount in the database
        List<CustomerAccount> customerAccountList = customerAccountRepository.findAll();
        assertThat(customerAccountList).hasSize(databaseSizeBeforeCreate + 1);
        CustomerAccount testCustomerAccount = customerAccountList.get(customerAccountList.size() - 1);
        assertThat(testCustomerAccount.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testCustomerAccount.getAccountType()).isEqualTo(DEFAULT_ACCOUNT_TYPE);
        assertThat(testCustomerAccount.getHasNatureDebit()).isEqualTo(DEFAULT_HAS_NATURE_DEBIT);
        assertThat(testCustomerAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }


    @Test
    @Transactional
    public void getAllCustomerAccounts() throws Exception {
        // Initialize the database
        customerAccountRepository.saveAndFlush(customerAccount);

        // Get all the CustomerAccountList
        restCustomerAccountMockMvc.perform(get("/api/customer-accounts")
                .param("customer-id", DEFAULT_CUSTOMER_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE)))
            .andExpect(jsonPath("$.[*].hasNatureDebit").value(hasItem(DEFAULT_HAS_NATURE_DEBIT.toString())));
    }
    
    @Test
    @Transactional
    public void getCustomerAccount() throws Exception {
        // Initialize the database
        this.customerAccount = customerAccountRepository.saveAndFlush(this.customerAccount);

        // Get the customerAccountDTO
        restCustomerAccountMockMvc.perform(get("/api/customer-accounts/{id}", this.customerAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(customerAccount.getId()))
                .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID))
                .andExpect(jsonPath("$.accountType").value(DEFAULT_ACCOUNT_TYPE))
                .andExpect(jsonPath("$.hasNatureDebit").value(DEFAULT_HAS_NATURE_DEBIT.toString()))
                .andExpect(jsonPath("$.balance").value(BigDecimal.ZERO));
    }

    @Test
    @Transactional
    public void getNonExistingCustomerAccount() throws Exception {
        // Get the customerAccountDTO
        restCustomerAccountMockMvc.perform(get("/api/customer-accounts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void equalsVerifier() {
        CustomerAccount customerAccount1 = new CustomerAccount();
        customerAccount1.setId(1L);
        CustomerAccount customerAccount2 = new CustomerAccount();
        customerAccount2.setId(customerAccount1.getId());
        assertThat(customerAccount1).isEqualTo(customerAccount2);
        customerAccount2.setId(2L);
        assertThat(customerAccount1).isNotEqualTo(customerAccount2);
        customerAccount1.setId(null);
        assertThat(customerAccount1).isNotEqualTo(customerAccount2);
    }
}
