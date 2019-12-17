package com.leovegasgroup.generalledger.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leovegasgroup.generalledger.domain.enumeration.Flag;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "balance", precision = 21, scale = 2, nullable = false)
    private BigDecimal balance;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_debit")
    private Flag isDebit;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @NotNull
    @Column(name = "app_transaction_id", nullable = false, unique = true)
    private String appTransactionId;

    @ManyToOne
    @JsonIgnore
    private CustomerAccount customerAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Transaction balance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Transaction amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Flag getIsDebit() {
        return isDebit;
    }

    public Transaction isDebit(Flag isDebit) {
        this.isDebit = isDebit;
        return this;
    }

    public void setIsDebit(Flag isDebit) {
        this.isDebit = isDebit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Transaction timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppTransactionId() {
        return appTransactionId;
    }

    public Transaction appTransactionId(String appTransactionId) {
        this.appTransactionId = appTransactionId;
        return this;
    }

    public void setAppTransactionId(String appTransactionId) {
        this.appTransactionId = appTransactionId;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public Transaction customerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
        return this;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", balance=" + getBalance() +
            ", amount=" + getAmount() +
            ", isDebit='" + getIsDebit() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", appTransactionId='" + getAppTransactionId() + "'" +
            "}";
    }
}
