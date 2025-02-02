package com.NBE3_4_2_Team4.domain.board.question.dto.response;

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionDto;

public record QuestionWriteResDto(
        QuestionDto item,
        long totalCount
) {}
