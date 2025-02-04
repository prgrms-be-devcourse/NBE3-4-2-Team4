package com.NBE3_4_2_Team4.global.exceptions;

public class RecommendAlreadyException extends RuntimeException {
    public RecommendAlreadyException() {
        super("이미 추천한 게시글입니다.");
    }
}
