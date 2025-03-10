package com.NBE3_4_2_Team4.global.exceptions;

import com.NBE3_4_2_Team4.global.config.AppConfig.Companion.log

class InValidAccessException(val remoteAddr: String, url: String) :
    RuntimeException("someone with ip $remoteAddr accessed $url in Invalid way") {

    init {
        log.warn(this.message, this)
    }
}
