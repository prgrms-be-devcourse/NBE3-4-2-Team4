package com.NBE3_4_2_Team4.global.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InValidAccessException extends RuntimeException {
    public InValidAccessException(String remoteAddr, String url) {
        super(String.format("someone with ip %s accessed %s in Invalid way", remoteAddr, url));
        log.warn(this.getMessage(), this);
    }
}
