package com.leovegasgroup.generalledger.web.rest;

import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.service.CustomerAccountService;
import com.leovegasgroup.generalledger.service.dto.CustomerAccountDTO;
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
 * REST controller for managing {@link CustomerAccount}.
 */
@RestController
@RequestMapping("/api")
public class CustomerAccountResource {

    private final Logger log = LoggerFactory.getLogger(CustomerAccountResource.class);


    @Value("${spring.application.name}")
    private String applicationName;

    private final CustomerAccountService customerAccountService;

    public CustomerAccountResource(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }

    /**
     * {@code POST  /customer-accounts} : Create a new customerAccount.
     *
     * @param customerAccountDTO the customerAccount to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerAccount, or with status {@code 400 (Bad Request)} if the customerAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customer-accounts")
    public ResponseEntity<CustomerAccount> createCustomerAccount(@Valid @RequestBody CustomerAccountDTO customerAccountDTO) throws URISyntaxException {
        log.debug("REST request to save CustomerAccount : {}", customerAccountDTO);
        CustomerAccount result = customerAccountService.save(customerAccountDTO);
        return ResponseEntity.created(new URI("/api/customer-accounts/" + result.getId()))
            .body(result);
    }

    /**
     * {@code GET  /customer-accounts} : get all the "customerId" customerAccounts
     *
     * @param userId the unique identifier of the user
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerAccounts in body.
     */
    @GetMapping("/customer-accounts")
    public List<CustomerAccount> getAllCustomerAccounts(@RequestParam(name = "customer-id") String userId) {
        log.debug("REST request to get all CustomerAccounts");
        return customerAccountService.getAllByCustomerId(userId);
    }

    /**
     * {@code GET  /customer-accounts/:id} : get the "id" customerAccount.
     *
     * @param id the id of the customerAccount.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerAccount, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customer-accounts/{id}")
    public ResponseEntity<CustomerAccount> getCustomerAccount(@PathVariable Long id) {
        log.debug("REST request to get CustomerAccount : {}", id);
        Optional<CustomerAccount> customerAccount = customerAccountService.findById(id);
        return ResponseUtil.wrapOrNotFound(customerAccount);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The '%s' url parameter is mandatory", name));
    }

}
