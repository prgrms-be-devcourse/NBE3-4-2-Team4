package com.NBE3_4_2_Team4.global.api.iamport.v1.constants;

public class IamportConstants {

    /* URL */
    public static final String IAMPORT_BASE_URL = "https://api.iamport.kr";

    public static final String IAMPORT_GENERATE_TOKEN_URL = IAMPORT_BASE_URL + "/users/getToken";

    public static final String IAMPORT_VALIDATE_BANK_ACCOUNT_URL = IAMPORT_BASE_URL + "/vbanks/holder";

    public static final String IAMPORT_GET_BANK_CODES_URL = IAMPORT_BASE_URL + "/banks";

    public static final String IAMPORT_FIND_BANK_NAME_URL = IAMPORT_BASE_URL + "/banks/{bankCode}";

    public static final String IAMPORT_GET_PAYMENT_HISTORY_URL = IAMPORT_BASE_URL + "/payments/{impUid}";

    public static final String IAMPORT_CANCEL_PAYMENT_URL = IAMPORT_BASE_URL + "/payments/cancel";

    /* Token */
    public static final String IAMPORT_TOKEN_REDIS_KEY = "iamport:access_token:";
}