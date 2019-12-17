package com.leovegasgroup.generalledger.web.rest;

import com.leovegasgroup.generalledger.domain.Transaction;
import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.repository.TransactionRepository;
import com.leovegasgroup.generalledger.repository.CustomerAccountRepository;
import com.leovegasgroup.generalledger.service.TransactionService;
import com.leovegasgroup.generalledger.service.dto.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.leovegasgroup.generalledger.domain.Transaction}.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);


    @Value("${spring.application.name}")
    private String applicationName;

    private final TransactionRepository transactionRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final TransactionService transactionService;

    public TransactionResource(TransactionRepository transactionRepository, CustomerAccountRepository customerAccountRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.transactionService = transactionService;
    }


    /**
     * {@code GET  /transactions} : get all the "customerAccountId" transactions.
     *
     * @param customerAccountId the customer account ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactions in body.
     */
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions(@RequestParam(name = "customer-account-id") Long customerAccountId) {
        log.debug("REST request to get all Transactions");
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(customerAccountId);
        return transactionRepository.getAllByCustomerAccount(customerAccount);
    }

    /**
     * {@code GET  /transactions/:transactionId} : get the "transactionId" transaction.
     *
     * @param transactionId the transactionId of the transaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId) {
        log.debug("REST request to get Transaction : {}", transactionId);
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        return ResponseUtil.wrapOrNotFound(transaction);
    }

    /**
     * {@code POST  /user-transactions} : Issue a transaction for the "customer-account-id" CustomerAccount.
     *
     * @return transaction
     */
    @PostMapping("/transactions")
    public ResponseEntity<Transaction> issueTransaction(@Valid @RequestBody TransactionDTO transactionDTO) throws URISyntaxException {
        CustomerAccount customerAccount = customerAccountRepository.findById(transactionDTO.getCustomerAccountId())
                .orElseThrow(() -> {
                    String message = String.format("'%d' customer-account not found", transactionDTO.getCustomerAccountId());
                    log.warn(message);
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
                });
        transactionDTO.setCustomerAccount(customerAccount);
        Transaction transaction = transactionService.issueTransaction(transactionDTO);
        return ResponseEntity.created(new URI("/api/transactions/" + transaction.getId()))
                .body(transaction);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The '%s' url parameter is mandatory", name));
    }
    

}
