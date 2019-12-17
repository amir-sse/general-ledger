package com.leovegasgroup.generalledger.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "A duplicate transaction ID found")
public class DuplicateRequiredTransactionId extends RuntimeException {
    private static final long serialVersionUID = 1414674131774967208L;
}
