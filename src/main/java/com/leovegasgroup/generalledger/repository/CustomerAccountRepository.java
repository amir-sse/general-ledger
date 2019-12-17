package com.leovegasgroup.generalledger.repository;

import com.leovegasgroup.generalledger.domain.CustomerAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the CustomerAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {


    /**
     *
     * @param customerId the unique identifier of the user
     * @return all the "customerId" customerAccounts
     */
    List<CustomerAccount> getAllByCustomerId(String customerId);

    /**
     * gets the 'id' customer-account and locks for update
     * @param id id
     * @return CustomerAccount
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CustomerAccount> getById(Long id);
}
