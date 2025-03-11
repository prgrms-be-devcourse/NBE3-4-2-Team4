package com.NBE3_4_2_Team4.global.api.iamport.v1.authentication

interface IamportAuthenticationService {

    fun generateAccessToken(memberId: Long): String?

    fun getAccessToken(memberId: Long): String?
}