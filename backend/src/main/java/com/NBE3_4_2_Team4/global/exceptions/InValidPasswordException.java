package com.NBE3_4_2_Team4.global.exceptions;

public class InValidPasswordException extends RuntimeException {
    public InValidPasswordException() {
        super("Invalid password");
    }
}
