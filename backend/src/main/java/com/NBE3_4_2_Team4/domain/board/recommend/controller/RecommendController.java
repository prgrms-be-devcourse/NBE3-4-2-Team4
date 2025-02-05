package com.NBE3_4_2_Team4.domain.board.recommend.controller;

import com.NBE3_4_2_Team4.domain.board.recommend.service.RecommendService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions/{questionId}/recommend")
public class RecommendController {
    private final RecommendService recommendService;

    @PostMapping
    public RsData<Void> recommend(@PathVariable long questionId) { // 게시글 추천
        Member member = AuthManager.getMemberFromContext();
        recommendService.recommend(questionId, member);

        return new RsData<>(
                "200-1",
                "게시글 추천이 완료되었습니다."
        );
    }

    @DeleteMapping
    public RsData<Void> cancelRecommend(@PathVariable long questionId) { // 게시글 추천 취소
        Member member = AuthManager.getMemberFromContext();
        recommendService.cancelRecommend(questionId, member);

        return new RsData<>(
                "200-1",
                "게시글 추천을 취소하었습니다."
        );
    }
}
