package com.NBE3_4_2_Team4.domain.board.recommend.service

import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository
import com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend
import com.NBE3_4_2_Team4.domain.board.recommend.repository.RecommendRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecommendService(
        private val questionRepository: QuestionRepository,
        private val recommendRepository: RecommendRepository
) {
    @Transactional
    fun toggleRecommend(questionId: Long, member: Member): Boolean {
        val question = questionRepository.findById(questionId).orElseThrow {
            ServiceException("404-1", "게시글이 존재하지 않습니다.")
        }

        // 추천 여부 확인
        return if (recommendRepository.existsByQuestionAndMember(question, member)) {
            cancelRecommend(questionId, member) // 추천 취소
            false
        } else {
            recommend(questionId, member) // 추천 추가
            true
        }

    }

    @Transactional
    fun recommend(questionId: Long, member: Member) {
        val question = questionRepository.findById(questionId).orElseThrow {
            ServiceException("404-1", "게시글이 존재하지 않습니다.")
        }

        if (recommendRepository.existsByQuestionAndMember(question, member)) { // 중복 추천 방지
            throw ServiceException("400-1", "이미 추천한 게시글입니다.")
        }
        if (question.author != member) { // 본인 글 추천 방지
            throw ServiceException("400-2", "자신의 게시글은 추천할 수 없습니다.")
        }

        val recommend = Recommend(
                question = question,
                member = member
        )

        recommendRepository.save(recommend)
    }

    @Transactional
    fun cancelRecommend(questionId: Long, member: Member) {
        val question = questionRepository.findById(questionId).orElseThrow {
            ServiceException("404-1", "게시글이 존재하지 않습니다.")
        }

        val recommend = recommendRepository.findByQuestionAndMember(question, member)
                .orElseThrow { NoSuchElementException("해당 추천이 존재하지 않습니다.") }
        recommendRepository.delete(recommend)
    }
}
