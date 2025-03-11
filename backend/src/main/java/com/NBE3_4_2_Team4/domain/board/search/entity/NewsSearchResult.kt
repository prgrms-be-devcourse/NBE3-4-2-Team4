package com.NBE3_4_2_Team4.domain.board.search.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.Embeddable

@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class NewsSearchResult(
    val title: String = "",
    val link: String = "",
    val description: String = ""
)
