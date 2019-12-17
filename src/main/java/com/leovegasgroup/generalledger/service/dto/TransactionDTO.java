package com.leovegasgroup.generalledger.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.domain.enumeration.Flag;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * POST DTO
 */
public class TransactionDTO {
    @NotNull(message = "Transaction type is mandatory")
    private Flag isDebit;
    @NotNull(message = "Transaction amount is mandatory")
    @DecimalMin("1.00")
    private BigDecimal amount;
    @NotEmpty(message = "The required transaction ID is missed")
    private String appTransactionId;
    @NotNull
    private Long customerAccountId;
    @JsonIgnore
    private CustomerAccount customerAccount;

    public Flag getIsDebit() {
        return isDebit;
    }

    public TransactionDTO isDebit(Flag isDebit) {
        this.isDebit = isDebit;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionDTO amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAppTransactionId() {
        return appTransactionId;
    }

    public TransactionDTO appTransactionId(String appTransactionId) {
        this.appTransactionId = appTransactionId;
        return this;
    }

    public void setAppTransactionId(String appTransactionId) {
        this.appTransactionId = appTransactionId;
    }

    public Long getCustomerAccountId() {
        return customerAccountId;
    }

    public TransactionDTO customerAccountId(Long customerAccountId) {
        this.customerAccountId = customerAccountId;
        return this;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }
}
