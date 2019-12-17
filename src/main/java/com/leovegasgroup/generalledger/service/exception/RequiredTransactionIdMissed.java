package com.leovegasgroup.generalledger.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The required transaction ID is missed")
public class RequiredTransactionIdMissed extends RuntimeException {
    private static final long serialVersionUID = -5171250900585124935L;
}
