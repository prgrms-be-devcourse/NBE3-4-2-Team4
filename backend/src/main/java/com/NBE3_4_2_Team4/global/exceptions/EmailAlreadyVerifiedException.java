package com.NBE3_4_2_Team4.global.exceptions;

public class EmailAlreadyVerifiedException extends RuntimeException {
    public EmailAlreadyVerifiedException() {
        super("Email already verified");
    }
}
