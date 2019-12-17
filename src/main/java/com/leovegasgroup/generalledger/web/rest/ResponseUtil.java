package com.leovegasgroup.generalledger.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class ResponseUtil {
    public static <T> ResponseEntity<T> wrapOrNotFound(Optional<T> result) {
        if (result.isPresent()) {
            return ResponseEntity.ok().body(result.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested data not found");
        }
    }

}
