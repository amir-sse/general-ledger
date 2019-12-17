package com.leovegasgroup.generalledger.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The transaction amount exceeds the account balance")
public class InsufficientBalanceException extends RuntimeException {
    private static final long serialVersionUID = -3631249895369824941L;
}
