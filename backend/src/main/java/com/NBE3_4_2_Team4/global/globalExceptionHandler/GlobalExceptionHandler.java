package com.NBE3_4_2_Team4.global.globalExceptionHandler;

import com.NBE3_4_2_Team4.global.config.AppConfig;
import com.NBE3_4_2_Team4.global.exceptions.InValidAccessException;
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException;
import com.NBE3_4_2_Team4.global.exceptions.PointClientException;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RsData<Empty>> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RsData<>(
                        "404-1",
                        "해당 데이터가 존재하지 않습니다."
                ));
    }

    @ExceptionHandler(InValidAccessException.class)
    public ResponseEntity<RsData<Empty>> handleInValidAccessException(InValidAccessException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "400-1",
                        e.getMessage() // 커스텀 메시지 사용
                ));
    }
  
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Empty>> handle(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .map(error -> error.getField() + "-" + error.getCode() + "-" + error.getDefaultMessage())
                .sorted(Comparator.comparing(String::toString))
                .collect(Collectors.joining("\n"));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "400-1",
                        message
                ));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RsData<Empty>> handle(ServiceException ex) {
        RsData<Empty> rsData = ex.getRsData();

        return ResponseEntity
                .status(rsData.getStatusCode())
                .body(rsData);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<RsData<Empty>> handleMemberNotFoundException(MemberNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RsData<>(
                        "404-1",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(PointClientException.class)
    public ResponseEntity<RsData<Empty>> handlePointClientException(PointClientException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "400-1",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<RsData<Empty>> handle(MaxUploadSizeExceededException ex) {
        if (AppConfig.isNotProd()) ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "413-2",
                        "업로드되는 개별 파일의 용량은 %s(을)를 초과할 수 없습니다.".formatted(AppConfig.getSpringServletMultipartMaxFileSize())
                ));
    }
}
