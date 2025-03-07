package com.NBE3_4_2_Team4.global.exceptions;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InValidAccessException(val remoteAddr: String, url: String) :
    RuntimeException("someone with ip $remoteAddr accessed $url in Invalid way") {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(InValidAccessException::class.java)
    }

    init {
        log.warn(this.message, this)
    }
}
