package com.NBE3_4_2_Team4.standard.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageDto<T> {
    private final int currentPageNumber;
    private final int pageSize;
    private final long totalPages;
    private final long totalItems;
    private final boolean hasMore;
    private final List<T> items;

    public PageDto(Page<T> page) {
        this.currentPageNumber = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
        this.hasMore = page.hasNext();
        this.items = page.getContent();
    }
}
