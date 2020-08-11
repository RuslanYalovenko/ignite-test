/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.rest;

import com.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DiffControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(DiffController.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity handleException(ValidationException e) {
        LOG.info(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
