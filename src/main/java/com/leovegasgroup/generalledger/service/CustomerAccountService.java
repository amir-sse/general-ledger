package com.leovegasgroup.generalledger.service;

import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.domain.Transaction;
import com.leovegasgroup.generalledger.repository.CustomerAccountRepository;
import com.leovegasgroup.generalledger.service.dto.CustomerAccountDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerAccountService {

    private final CustomerAccountRepository customerAccountRepository;

    public CustomerAccountService(CustomerAccountRepository customerAccountRepository) {
        this.customerAccountRepository = customerAccountRepository;
    }

    /**
     * Calculates balance from the 'customerAccount'
     * @param customerAccount customer account
     * @return balance
     */
    public BigDecimal calculateBalance(CustomerAccount customerAccount) {
        Transaction headTransaction = customerAccount.getHeadTransaction();
        if (headTransaction == null) {
            return BigDecimal.ZERO;
        } else if (BigDecimal.ZERO.equals(headTransaction.getAmount())) {
            return headTransaction.getBalance();
        } else {
            return headTransaction.getIsDebit().equals(customerAccount.getHasNatureDebit())
                    ? headTransaction.getBalance().add(headTransaction.getAmount())
                    : headTransaction.getBalance().subtract(headTransaction.getAmount());
        }
    }

    /**
     * saves a new CustomerAccount
     * @param customerAccountDTO CustomerAccount details
     * @return saved CustomerAccount
     */
    public CustomerAccount save(CustomerAccountDTO customerAccountDTO) {
        CustomerAccount customerAccount = new CustomerAccount()
                .customerId(customerAccountDTO.getCustomerId())
                .hasNatureDebit(customerAccountDTO.getHasNatureDebit())
                .accountType(customerAccountDTO.getAccountType());
        customerAccount = customerAccountRepository.save(customerAccount);
        customerAccount.setBalance(calculateBalance(customerAccount));
        return customerAccount;
    }

    /**
     * Gets the 'id' account including balance
     * @param id CustomerAccount id
     * @return CustomerAccount including balance
     */
    public Optional<CustomerAccount> findById(Long id) {
        Optional<CustomerAccount> customerAccountOptional = customerAccountRepository.findById(id);
        return customerAccountOptional.map(customerAccount -> {
            customerAccount.setBalance(calculateBalance(customerAccount));
            return customerAccount;
        });
    }

    /**
     * Gets all the 'customerId' found accounts including balance
     * @param customerId customerId
     * @return found accounts
     */
    public List<CustomerAccount> getAllByCustomerId(String customerId) {
        return customerAccountRepository.getAllByCustomerId(customerId)
                .stream()
                .peek(customerAccount -> customerAccount.setBalance(calculateBalance(customerAccount)))
                .collect(Collectors.toList());
    }
}
