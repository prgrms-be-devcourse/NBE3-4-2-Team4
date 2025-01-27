package com.NBE3_4_2_Team4.global.exceptions;

public class InValidPasswordException extends RuntimeException {
    public InValidPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
