package com.NBE3_4_2_Team4.global.exceptions;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException() {
        super("게시글이 존재하지 않습니다.");
    }
}
