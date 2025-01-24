package com.NBE3_4_2_Team4.global.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RsData<T> {
    private String rsCode;
    private String msg;
    private T data;

    public RsData(String rsCode, String msg) {
        this(rsCode, msg, null);
    }
}
