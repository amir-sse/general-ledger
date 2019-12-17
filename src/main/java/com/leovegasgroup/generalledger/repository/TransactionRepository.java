package com.leovegasgroup.generalledger.repository;

import com.leovegasgroup.generalledger.domain.CustomerAccount;
import com.leovegasgroup.generalledger.domain.Transaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> getAllByCustomerAccount(CustomerAccount customerAccount);

    Optional<Transaction> getFirstByAppTransactionId(String appTransactionId);

}
