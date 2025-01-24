package com.NBE3_4_2_Team4.global.rsData;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class RsData<T> {
    private String rsCode;
    private String msg;
    private T data;

    public RsData(String rsCode, String msg) {
        this(rsCode, msg, null);
    }
}
