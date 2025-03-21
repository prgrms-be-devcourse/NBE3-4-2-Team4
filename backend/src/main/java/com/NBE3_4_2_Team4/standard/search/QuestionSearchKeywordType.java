package com.NBE3_4_2_Team4.standard.search;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum QuestionSearchKeywordType {
    ALL("전체"),
    TITLE("제목"),
    CONTENT("내용"),
    AUTHOR("작성자"),
    ANSWER_CONTENT("답변 내용");

    private final String value;
}