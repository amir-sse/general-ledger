package com.leovegasgroup.generalledger.service.dto;

import com.leovegasgroup.generalledger.domain.enumeration.Flag;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * POST DTO
 */
public class CustomerAccountDTO {

    @NotEmpty(message = "This field states the real owner and is mandatory.")
    private String customerId;
    private String accountType;
    @NotNull(message = "This field is mandatory.")
    private Flag hasNatureDebit;

    public String getCustomerId() {
        return customerId;
    }

    public CustomerAccountDTO customerId(String customerId) {
        this.customerId = customerId;
        return this;

    }

    public String getAccountType() {
        return accountType;
    }

    public CustomerAccountDTO accountType(String accountType) {
        this.accountType = accountType;
        return this;
    }

    public Flag getHasNatureDebit() {
        return hasNatureDebit;
    }

    public CustomerAccountDTO hasNatureDebit(Flag hasNatureDebit) {
        this.hasNatureDebit = hasNatureDebit;
        return this;
    }
}
