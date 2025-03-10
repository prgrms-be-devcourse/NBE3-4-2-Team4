package com.NBE3_4_2_Team4.domain.board.search.controller;

import com.NBE3_4_2_Team4.domain.board.search.service.TrendSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trend-search")
@RequiredArgsConstructor
public class TrendSearchController {
    private final TrendSearchService trendSearchService;

    @GetMapping
    public String getInfoSearch(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "3") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        return trendSearchService.getSearch(query, pageSize, page);
    }
}
