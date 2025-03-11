package com.NBE3_4_2_Team4.global.api.iamport.v1.constants

object IamportConstants {

    /* URL */
    const val IAMPORT_BASE_URL = "https://api.iamport.kr"

    const val IAMPORT_GENERATE_TOKEN_URL = "$IAMPORT_BASE_URL/users/getToken"

    const val IAMPORT_VALIDATE_BANK_ACCOUNT_URL = "$IAMPORT_BASE_URL/vbanks/holder"

    const val IAMPORT_GET_BANK_CODES_URL = "$IAMPORT_BASE_URL/banks"

    const val IAMPORT_FIND_BANK_NAME_URL = "$IAMPORT_BASE_URL/banks/{bankCode}"

    const val IAMPORT_GET_PAYMENT_HISTORY_URL = "$IAMPORT_BASE_URL/payments/{impUid}"

    const val IAMPORT_CANCEL_PAYMENT_URL = "$IAMPORT_BASE_URL/payments/cancel"

    /* Token */
    const val IAMPORT_TOKEN_REDIS_KEY = "iamport:access_token:"
}