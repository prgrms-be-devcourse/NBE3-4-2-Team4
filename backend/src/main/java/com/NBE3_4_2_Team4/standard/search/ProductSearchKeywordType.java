package com.NBE3_4_2_Team4.standard.search;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProductSearchKeywordType {
    ALL("전체"),
    NAME("상품명"),
    CATEGORY("카테고리");

    private final String value;
}