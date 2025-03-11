package com.NBE3_4_2_Team4.global.api.iamport.v1.payment

import com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_CANCEL_PAYMENT_URL
import com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_GET_PAYMENT_HISTORY_URL
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

@Service
class IamportPaymentServiceImpl(

    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper

    ) : IamportPaymentService {

    private val logger = KotlinLogging.logger {}

    override fun getPaymentHistory(
        impAccessToken: String,
        impUid: String
    ): GetPayment? {

        return try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $impAccessToken")
            }

            val request = HttpEntity<Void>(headers)
            val response: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
                IAMPORT_GET_PAYMENT_HISTORY_URL,
                HttpMethod.GET,
                request,
                object : ParameterizedTypeReference<Map<String, Any>>() {},
                impUid
            )

            response.body?.get("response")?.let {
                objectMapper.convertValue(it, GetPayment::class.java)
            }
        } catch (e: RestClientResponseException) {
            logger.error { "API Error: ${e.statusCode} - ${e.responseBodyAsString}" }
            null
        } catch (e: Exception) {
            logger.error { "Unexpected Error: ${e.message}" }
            null
        }
    }

    override fun cancelPayment(
        impAccessToken: String,
        cancelPaymentInfo: IamportPaymentRequestDto.CancelPaymentInfo
    ): GetPayment? {

        return try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                accept = listOf(MediaType.APPLICATION_JSON)
                set("Authorization", "Bearer $impAccessToken")
            }

            val requestBody = LinkedMultiValueMap<String, String>().apply {
                add("imp_uid", cancelPaymentInfo.impUid)
                add("merchant_uid", cancelPaymentInfo.merchantUid)
                add("amount", cancelPaymentInfo.amount.toString())
                add("reason", cancelPaymentInfo.reason)
            }

            val request = HttpEntity(requestBody, headers)
            val response: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
                IAMPORT_CANCEL_PAYMENT_URL,
                HttpMethod.POST,
                request,
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )

            response.body?.get("response")?.let {
                objectMapper.convertValue(it, GetPayment::class.java)
            }
        } catch (e: RestClientResponseException) {
            logger.error { "API Error: ${e.statusCode} - ${e.responseBodyAsString}" }
            null
        } catch (e: Exception) {
            logger.error { "Unexpected Error: ${e.message}" }
            null
        }
    }
}