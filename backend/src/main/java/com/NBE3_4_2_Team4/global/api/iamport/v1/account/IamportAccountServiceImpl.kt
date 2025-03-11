package com.NBE3_4_2_Team4.global.api.iamport.v1.account

import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.BankInfo
import com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_FIND_BANK_NAME_URL
import com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_GET_BANK_CODES_URL
import com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_VALIDATE_BANK_ACCOUNT_URL
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class IamportAccountServiceImpl(

    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate

) : IamportAccountService {

    private val logger = KotlinLogging.logger {}

    override fun validateBankAccount(
        accessToken: String,
        bankAccount: IamportAccountRequestDto.BankAccountValidator
    ): String? {

        return try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $accessToken")
            }

            val url = UriComponentsBuilder.fromUriString(IAMPORT_VALIDATE_BANK_ACCOUNT_URL)
                .queryParam("bank_code", bankAccount.bankCode)
                .queryParam("bank_num", bankAccount.bankAccountNum)
                .toUriString()

            val request = HttpEntity<Void>(headers)
            val response: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )

            (response.body?.get("response") as? Map<*, *>)?.get("bank_holder")?.toString()?.also { name ->
                logger.info { "Bank Account validated: [${bankAccount.bankCode}, ${bankAccount.bankAccountNum} -> $name]" }
            }

        } catch (ex: RestClientResponseException) {
            logger.error { "API Error: ${ex.message}" }
            null
        } catch (ex: Exception) {
            logger.error { "Unexpected Error: ${ex.message}" }
            null
        }
    }

    override fun getBankCodes(
        accessToken: String
    ): List<BankInfo> {

        return try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $accessToken")
            }

            val request = HttpEntity<Void>(headers)
            val response: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
                IAMPORT_GET_BANK_CODES_URL,
                HttpMethod.GET,
                request,
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )

            response.body?.get("response")?.let {
                objectMapper.convertValue(it, object : TypeReference<List<BankInfo>>() {})
            } ?: emptyList()

        } catch (ex: RestClientResponseException) {
            logger.error { "API Error: ${ex.message}" }
            emptyList()
        } catch (ex: Exception) {
            logger.error { "Unexpected Error: ${ex.message}" }
            emptyList()
        }
    }

    override fun findBankNameByBankCode(
        accessToken: String,
        bankCode: String
    ): BankInfo? {

        return try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $accessToken")
            }

            val request = HttpEntity<Void>(headers)
            val response: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
                IAMPORT_FIND_BANK_NAME_URL,
                HttpMethod.GET,
                request,
                object : ParameterizedTypeReference<Map<String, Any>>() {},
                bankCode
            )

            (response.body?.get("response") as? Map<*, *>)?.let { responseData ->
                BankInfo(
                    code = responseData["code"] as String,
                    name = responseData["name"] as String
                )
            }

        } catch (ex: RestClientResponseException) {
            logger.error { "API Error: ${ex.message}" }
            null
        } catch (ex: Exception) {
            logger.error { "Unexpected Error: ${ex.message}" }
            null
        }
    }
}