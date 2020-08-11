/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.exception;

public class ValidationException extends Exception {

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }
}
