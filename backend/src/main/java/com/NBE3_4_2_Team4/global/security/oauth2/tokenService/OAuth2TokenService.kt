package com.NBE3_4_2_Team4.global.security.oauth2.tokenService

interface OAuth2TokenService {
    fun getFreshAccessToken(refreshToken: String?): String?
}