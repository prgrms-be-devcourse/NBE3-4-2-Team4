package com.NBE3_4_2_Team4.global.rsData;

import com.NBE3_4_2_Team4.standard.base.Empty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.lang.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class RsData<T> {
    public static final RsData<Empty> OK = new RsData<>("200-1", "OK", new Empty());

    @NonNull
    public final String resultCode;

    @NonNull
    public final String msg;

    @NonNull
    private final T data;

    public RsData(String resultCode, String msg, T data) {
        this.resultCode = resultCode;
        this.msg = msg;
        this.data = data;
    }

    public RsData(String resultCode, String msg) {
        this(resultCode, msg, (T) new Empty());
    }

    @JsonIgnore
    public int getStatusCode() {
        return Integer.parseInt(resultCode.split("-")[0]);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return getStatusCode() < 400;
    }

    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }
}
