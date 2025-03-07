package com.NBE3_4_2_Team4.global.exceptions

import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty

class ServiceException(
    private val resultCode: String,
    private val msg: String
) : RuntimeException(
    "$resultCode : $msg"
) {
    val rsData: RsData<Empty>
        get() = RsData(resultCode, msg)
}
