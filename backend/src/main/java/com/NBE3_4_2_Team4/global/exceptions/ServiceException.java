package com.NBE3_4_2_Team4.global.exceptions;

import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;

public class ServiceException extends RuntimeException {
    private final String resultCode;
    private final String msg;

    public ServiceException(String resultCode, String msg) {
        super(resultCode + " : " + msg);
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public RsData<Empty> getRsData() {
        return new RsData<>(resultCode, msg);
    }
}
