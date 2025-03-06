package com.NBE3_4_2_Team4.global.rsData

import com.NBE3_4_2_Team4.standard.base.Empty
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RsData<T>(
    val resultCode: String,
    val msg: String,
    val data: T = Empty() as T
) {
    companion object {
        @JvmField
        val OK = RsData("200-1", "OK", Empty())
    }

    constructor(resultCode: String, msg: String) : this(resultCode, msg, Empty() as T)

    @get:JsonIgnore
    val statusCode: Int
        get() = resultCode.split("-")[0].toInt()

    @JsonIgnore
    val isSuccess = statusCode < 400

    @JsonIgnore
    val isFail = !isSuccess
}
