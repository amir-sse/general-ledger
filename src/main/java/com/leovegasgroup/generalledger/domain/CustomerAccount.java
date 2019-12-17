package com.leovegasgroup.generalledger.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leovegasgroup.generalledger.domain.enumeration.Flag;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A CustomerAccount.
 */
@Entity
@Table(name = "customer_account")
public class CustomerAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "account_type")
    private String accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "has_nature_debit", nullable = false)
    private Flag hasNatureDebit;

    @Transient
    private BigDecimal balance;

    @JsonIgnore
    @OneToOne
    private Transaction headTransaction;

    @JsonIgnore
    @OneToMany(mappedBy = "customerAccount")
    private Set<Transaction> transactions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public CustomerAccount customerId(String customerId) {
        this.customerId = customerId;
        return this;
    }


    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public CustomerAccount accountType(String accountType) {
        this.accountType = accountType;
        return this;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Flag getHasNatureDebit() {
        return hasNatureDebit;
    }

    public CustomerAccount hasNatureDebit(Flag hasNatureDebit) {
        this.hasNatureDebit = hasNatureDebit;
        return this;
    }

    public void setHasNatureDebit(Flag hasNatureDebit) {
        this.hasNatureDebit = hasNatureDebit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public CustomerAccount balance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public Transaction getHeadTransaction() {
        return headTransaction;
    }

    public void setHeadTransaction(Transaction headTransaction) {
        this.headTransaction = headTransaction;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public CustomerAccount transactions(Set<Transaction> transactions) {
        this.transactions = transactions;
        return this;
    }

    public CustomerAccount addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setCustomerAccount(this);
        return this;
    }

    public CustomerAccount removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setCustomerAccount(null);
        return this;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerAccount)) {
            return false;
        }
        return id != null && id.equals(((CustomerAccount) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "CustomerAccount{" +
            "id=" + getId() +
            ", customerId='" + getCustomerId() + "'" +
            ", accountType='" + getAccountType() + "'" +
            ", hasNatureDebit='" + getHasNatureDebit() + "'" +
            "}";
    }
}
