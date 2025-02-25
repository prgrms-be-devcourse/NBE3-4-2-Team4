package com.NBE3_4_2_Team4.domain.board.message.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record MessageWriteReqDto(
        @NotNull @Length(min = 2)
        String content,
        @NotNull
        String receiverName
) {}
