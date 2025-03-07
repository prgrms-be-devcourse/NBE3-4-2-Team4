package com.NBE3_4_2_Team4.global.globalExceptionHandler

import com.NBE3_4_2_Team4.global.config.AppConfig
import com.NBE3_4_2_Team4.global.exceptions.InValidAccessException
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException
import com.NBE3_4_2_Team4.global.exceptions.PointClientException
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import java.util.stream.Collectors

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(e: NoSuchElementException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                RsData(
                    "404-1",
                    "해당 데이터가 존재하지 않습니다."
                )
            )
    }

    @ExceptionHandler(InValidAccessException::class)
    fun handleInValidAccessException(e: InValidAccessException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                RsData(
                    "400-1",
                    e.message!! // 커스텀 메시지 사용
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ResponseEntity<RsData<Empty>> {
        val message = ex.bindingResult
            .allErrors
            .stream()
            .filter { it is FieldError }
            .map { it as FieldError }
            .map { "${it.field}-${it.code}-${it.defaultMessage}" }
            .sorted(Comparator.comparing { it })
            .collect(Collectors.joining("\n"))

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                RsData(
                    "400-1",
                    message
                )
            )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        ServiceException::class
    )
    fun handle(ex: ServiceException): ResponseEntity<RsData<Empty>> {
        val rsData = ex.rsData

        return ResponseEntity
            .status(rsData.statusCode)
            .body(rsData)
    }

    @ExceptionHandler(MemberNotFoundException::class)
    fun handleMemberNotFoundException(e: MemberNotFoundException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                RsData(
                    "404-1",
                    e.message!!
                )
            )
    }

    @ExceptionHandler(PointClientException::class)
    fun handlePointClientException(e: PointClientException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                RsData(
                    "400-1",
                    e.message!!
                )
            )
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handle(ex: MaxUploadSizeExceededException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                RsData(
                    "413-2",
                    "업로드되는 개별 파일의 용량은 ${AppConfig.getSpringServletMultipartMaxFileSize()}(을)를 초과할 수 없습니다."
                )
            )
    }
}
