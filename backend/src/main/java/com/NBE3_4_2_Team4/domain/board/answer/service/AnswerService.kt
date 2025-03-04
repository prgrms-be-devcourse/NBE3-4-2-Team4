package com.NBE3_4_2_Team4.domain.board.answer.service

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer.answer
import com.NBE3_4_2_Team4.domain.board.answer.repository.AnswerRepository
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.security.AuthManager
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
class AnswerService(
    private val answerRepository: AnswerRepository,
    private val questionRepository: QuestionRepository
) {
    @Transactional
    fun write(questionId: Long, content: String): AnswerDto {
        val question = questionRepository.findById(questionId).orElseThrow {
            ServiceException(
                "404-1",
                "해당 질문글이 존재하지 않습니다."
            )
        }

        val actor = AuthManager.getNonNullMember()

        return AnswerDto(save(question, actor, content))
    }

    fun save(question: Question, author: Member, content: String): Answer {
        if (author.id === question.author.id) throw ServiceException("400-1", "작성자는 답변을 등록할 수 없습니다.")

        val answer = Answer(question, author, content)

        return answerRepository.save(answer)
    }

    fun count(): Long {
        return answerRepository.count()
    }

    fun findById(id: Long): Answer {
        val answer = answerRepository.findById(id).orElseThrow {
            ServiceException(
                "404-2",
                "해당 답변은 존재하지 않습니다."
            )
        }

        return answer
    }

    @Transactional(readOnly = true)
    fun item(id: Long): AnswerDto {
        return AnswerDto(findById(id))
    }

    @Transactional(readOnly = true)
    fun itemsAll(page: Int, pageSize: Int): Page<AnswerDto> {
        val pageable: Pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")))

        return answerRepository.findAll(pageable)
            .map { AnswerDto(it) }
    }

    @Transactional(readOnly = true)
    fun items(questionId: Long, page: Int, pageSize: Int): Page<AnswerDto> {
        val question = questionRepository.findById(questionId).orElseThrow {
            ServiceException(
                "404-1",
                "해당 질문글이 존재하지 않습니다."
            )
        }

        val pageable: Pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")))

        return answerRepository.findByQuestionAndSelected(question, pageable, false)
            .map { AnswerDto(it) }
    }

    @Transactional
    fun modify(id: Long, content: String): AnswerDto {
        val answer = findById(id)
        val actor = AuthManager.getNonNullMember()

        answer.checkActorCanModify(actor)
        answer.modify(content)

        return AnswerDto(answer)
    }

    @Transactional
    fun delete(id: Long) {
        val answer = findById(id)
        val actor = AuthManager.getNonNullMember()

        answer.checkActorCanDelete(actor)

        answerRepository.delete(answer)
    }
}
