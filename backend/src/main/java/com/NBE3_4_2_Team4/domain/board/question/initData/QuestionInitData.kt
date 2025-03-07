package com.NBE3_4_2_Team4.domain.board.question.initData

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionWriteReqDto
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionCategoryService
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import com.NBE3_4_2_Team4.domain.board.recommend.service.RecommendService
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.transaction.annotation.Transactional

@Order(1)
@Configuration
class QuestionInitData(
    private val questionService: QuestionService,
    private val memberRepository: MemberRepository,
    private val memberInitData: MemberInitData,
    private val recommendService: RecommendService,
    private val questionCategoryService: QuestionCategoryService
) {
    @Value("\${custom.initData.member.admin.username}")
    lateinit var adminUsername: String

    @Value("\${custom.initData.member.member1.username}")
    lateinit var member1Username: String

    @Autowired
    @Lazy
    lateinit var self: QuestionInitData

    @Bean
    fun questionInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            memberInitData.work() // admin이 먼저 생성되도록 하기 위해 호출
            self.initData()
        }
    }

    @Transactional
    fun initData() {
        if (questionService.count() > 0) return

        val admin = memberRepository.findByUsername(adminUsername).orElseThrow()
        val testUser = memberRepository.findByUsername(member1Username).orElseThrow()

        val categories = listOf("연애", "건강", "경제", "교육", "스포츠", "여행", "음식", "취업", "IT", "기타")
        for (category in categories) {
            questionCategoryService.createCategory(admin, category)
        }

        val questionDataList = listOf(
                createQuestion("성격 차이 극복 방법", "이 사람에게 어떻게 다가가야 할까요?", 1L, 100, AssetType.POINT),
                createQuestion("운동 후 단백질 섭취 시점", "운동 후 몇 분 내에 단백질을 먹는 게 가장 좋은가요?", 2L, 80, AssetType.POINT),
                createQuestion("주식 초보인데 어디서 시작해야 하나요?", "ETF랑 개별주 중에서 고민됩니다.", 3L, 200, AssetType.CASH),
                createQuestion("효율적인 공부 방법 추천", "시험을 앞두고 있는데 집중이 안 됩니다.", 4L, 90, AssetType.POINT),
                createQuestion("축구 연습 루틴 추천", "축구 실력을 키우기 위해 매일 어떤 연습을 하면 좋을까요?", 5L, 70, AssetType.POINT),
                createQuestion("일본 여행 코스 추천", "오사카랑 교토를 가려고 하는데, 일정 짜는 게 어렵네요.", 6L, 85, AssetType.POINT),
                createQuestion("맛집 찾는 팁", "현지인들만 아는 맛집을 찾는 법이 있을까요?", 7L, 60, AssetType.POINT),
                createQuestion("이직을 고민 중입니다.", "지금 다니는 회사에서 경력을 더 쌓아야 할까요?", 8L, 300, AssetType.CASH),
                createQuestion("React와 Vue 중 어느 것이 더 좋을까요?", "프론트엔드 개발을 배우려고 합니다.", 9L, 110, AssetType.POINT),
                createQuestion("퇴사 후 바로 창업하면 무모한가요?", "자본금이 많지 않은데도 가능할까요?", 3L, 95, AssetType.POINT),
                createQuestion("효율적인 다이어트 식단", "굶지 않고 건강하게 다이어트하는 방법이 궁금합니다.", 2L, 80, AssetType.POINT),
                createQuestion("해외 여행 시 필수 준비물", "자주 까먹는 필수 아이템이 있나요?", 6L, 70, AssetType.POINT),
                createQuestion("면접에서 자주 나오는 질문", "IT 기업 면접에서 어떤 질문을 준비해야 할까요?", 8L, 150, AssetType.CASH),
                createQuestion("노트북 추천 부탁드립니다.", "개발 용도로 사용할 예정입니다.", 9L, 120, AssetType.POINT),
                createQuestion("개발 공부를 시작하려는데 어떤 언어가 좋을까요?", "초보자가 입문하기 쉬운 언어가 궁금합니다.", 9L, 130, AssetType.POINT)
        );

        // 질문 생성
        for (i in questionDataList.indices) {
            val author = if (i % 2 == 0) admin else testUser
            val q = questionDataList[i]
            questionService.write(q.title, q.content, q.categoryId, author, q.amount, q.assetType)
        }

        recommendService.recommend(1L, testUser)
        recommendService.recommend(12L, admin)
    }

    private fun createQuestion(title: String, content: String, userId: Long, amounts: Long, assetType: AssetType): QuestionWriteReqDto {
        return QuestionWriteReqDto(title, content, userId, amounts, assetType)
    }
}
