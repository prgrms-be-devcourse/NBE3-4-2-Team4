package com.NBE3_4_2_Team4.standard.dto

import org.springframework.data.domain.Page

open class PageDto<T>(
    val currentPageNumber: Int,
    val pageSize: Int,
    val totalPages: Long,
    val totalItems: Long,
    val hasMore: Boolean,
    val items: List<T>
) {
    constructor(page: Page<T>) : this(
        currentPageNumber = page.number + 1,
        pageSize = page.size,
        totalPages = page.totalPages.toLong(),
        totalItems = page.totalElements,
        hasMore = page.hasNext(),
        items = page.content
    )
}
