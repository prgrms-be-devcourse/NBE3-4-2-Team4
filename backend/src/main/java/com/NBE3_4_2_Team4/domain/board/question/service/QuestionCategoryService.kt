package com.NBE3_4_2_Team4.domain.board.question.service

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionCategoryDto
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionCategoryService(
    private val questionCategoryRepository: QuestionCategoryRepository,
    private val questionRepository: QuestionRepository
) {
    @Transactional
    fun createCategory(actor: Member, name: String): QuestionCategoryDto {
        val category = QuestionCategory(name)

        category.checkActorCanCreate(actor)
        questionCategoryRepository.save(category)

        return QuestionCategoryDto(category)
    }

    @Transactional
    fun deleteCategory(actor: Member, id: Long) {
        val category = questionCategoryRepository.findById(id).orElseThrow()
        category.checkActorCanDelete(actor)

        // 해당 카테고리를 사용중인 질문이 있는지 확인
        if (questionRepository.existsByCategory(category)) {
            throw ServiceException("400-1", "해당 카테고리를 사용중인 질문이 있습니다.")
        }

        questionCategoryRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getCategories(): List<QuestionCategoryDto> {
        return questionCategoryRepository.findAll()
            .map { QuestionCategoryDto(it) }
    }
}
