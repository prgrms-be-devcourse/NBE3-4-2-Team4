package com.NBE3_4_2_Team4.domain.board.question.service;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.repository.AnswerRepository;
import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionDto;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.asset.point.service.PointService;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.standard.search.QuestionSearchKeywordType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final PointService pointService;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;

    public long count() {
        return questionRepository.count();
    }

    @Transactional
    public QuestionDto write(String title, String content, Long categoryId,
                             Member author, long amount, AssetType assetType) {
        QuestionCategory category = questionCategoryRepository.findById(categoryId).orElseThrow();
        Question question = Question.builder()
                .title(title)
                .content(content)
                .author(author)
                .category(category)
                .assetType(assetType)
                .amount(amount)
                .rankReceived(false)
                .build();

        //질문글 작성 시 포인트 차감
        pointService.deduct(author.getUsername(), amount, AssetCategory.QUESTION);
        questionRepository.save(question);

        return new QuestionDto(question);
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findLatest() {
        return questionRepository.findFirstByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public Page<QuestionDto> getQuestions(int page, int pageSize, String searchKeyword, long categoryId,
                                          QuestionSearchKeywordType keywordType, String asset) {
        AssetType assetType = AssetType.fromValue(asset);

        if (categoryId == 0 && asset.equals("ALL")) {
            return findByListed(page, pageSize, searchKeyword, keywordType);
        }
        // 카테고리 ID만 설정된 경우
        if (categoryId != 0 && assetType == AssetType.ALL) {
            return getQuestionsByCategory(categoryId, page, pageSize);
        }
        // assetType 만 설정된 경우
        if (categoryId == 0 && assetType != AssetType.ALL) {
            return getQuestionsByAssetType(assetType, page, pageSize);
        }

        // 둘 다 설정된 경우
        return getQuestionsByCategoryAndAssetType(categoryId, page, pageSize, assetType);
    }

    private Page<QuestionDto> findByListed(int page, int pageSize, String searchKeyword, QuestionSearchKeywordType searchKeywordType) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        return questionRepository.findByKw(searchKeywordType, searchKeyword, pageRequest)
                .map(QuestionDto::new);
    }

    private Page<QuestionDto> getQuestionsByCategoryAndAssetType(long categoryId, int page, int pageSize, AssetType assetType) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        QuestionCategory category = questionCategoryRepository.findById(categoryId).get();

        return questionRepository.findByCategoryAndAssetType(category, assetType, pageRequest)
                .map(QuestionDto::new);
    }

    private Page<QuestionDto> getQuestionsByCategory(long categoryId, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        QuestionCategory category = questionCategoryRepository.findById(categoryId).get();

        return questionRepository.findByCategory(category, pageRequest)
                .map(QuestionDto::new);
    }

    private Page<QuestionDto> getQuestionsByAssetType(AssetType assetType, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        return questionRepository.findByAssetType(assetType, pageRequest)
                .map(QuestionDto::new);
    }

    @Transactional(readOnly = true)
    public Page<QuestionDto> findByUserListed(int page, int pageSize, String username) {
        Member actor = memberRepository.findByUsername(username).get();
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        return questionRepository.findByAuthor(actor, pageRequest).map(QuestionDto::new);
    }

    @Transactional(readOnly = true)
    public Page<QuestionDto> findByRecommends(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepository.findRecommendedQuestions(pageRequest)
                .map(QuestionDto::new);
    }

    @Transactional(readOnly = true)
    public QuestionDto findById(long id) {
        Question question = questionRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );
        return new QuestionDto(question);
    }

    @Transactional
    public void delete(long id, Member actor) {
        Question question = questionRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );
        question.checkActorCanDelete(actor);
        questionRepository.delete(question);
    }

    @Transactional
    public QuestionDto update(long id, String title, String content, Member actor, long amount, long categoryId) {
        Question question = questionRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );
        question.checkActorCanModify(actor);
        QuestionCategory category = questionCategoryRepository.findById(categoryId).orElseThrow();

        if (amount < question.getAmount()) {
            throw new ServiceException("400-1", "포인트/캐시는 기존보다 낮게 설정할 수 없습니다.");
        }
        question.modify(title, content, amount, category);
        return new QuestionDto(question);
    }

    @Transactional
    public QuestionDto select(long id, long answerId) {
        Question question = questionRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );
        Answer answer = answerRepository.findById(answerId).get();
        Member actor = AuthManager.getMemberFromContext();

        if(question.isClosed())
            throw new ServiceException("400-1", "만료된 질문입니다.");

        if(actor == null)
            throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if(!actor.equals(question.getAuthor()))
            throw new ServiceException("403-2", "작성자만 답변을 채택할 수 있습니다.");

        if(question.getId() != answer.getQuestion().getId())
            throw new ServiceException("403-3", "해당 질문글 내의 답변만 채택할 수 있습니다.");

        answer.select();
        question.setSelectedAnswer(answer);
        question.setClosed(true);

        //질문글 채택 시 채택된 답변 작성자 포인트 지급
        pointService.accumulate(answer.getAuthor().getUsername(), question.getAmount(), AssetCategory.ANSWER);

        return new QuestionDto(question);
    }

    //7일 이상 질문들 closed 처리
    @Transactional
    public long closeQuestionsIfExpired() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(7);

        List<Question> expiredQuestions = questionRepository.findByCreatedAtBeforeAndClosed(expirationDate, false);

        for (Question question : expiredQuestions) {
            question.setClosed(true);

            if(question.getAnswers().size() == 0) {
                //답변자가 없는 경우 질문자에게 포인트 반환
                pointService.accumulate(question.getAuthor().getUsername(), question.getAmount(), AssetCategory.REFUND);

                continue;
            }

            long selectedPoint = question.getAmount() != 0 && question.getAmount() / question.getAnswers().size() > 1
                    ? question.getAmount() / question.getAnswers().size()
                    : 1; //최소 1포인트는 받을 수 있도록

            for(Answer answer : question.getAnswers()) {
                //채택된건 아니므로 selected는 false 상태에서 포인트 지급된 날짜만 입력
                answer.setSelectedAt();

                //분배된 포인트 지급
                pointService.accumulate(answer.getAuthor().getUsername(), selectedPoint, AssetCategory.EXPIRED_QUESTION);
            }
        }

        return expiredQuestions.size();
    }

    @Transactional
    public void awardRankingPoints() { // 랭킹 순위 질문 포인트 지급
        List<Question> topQuestions = questionRepository.findRecommendedQuestions();

        int[] points = {1000, 500, 300}; // 1등 1000, 2등 500, 3등 300포인트 지급
        int currentRank = 1; // 현재 순위
        long currentRecommendCount = 0; // 현재 순위의 추천수
        int assignedRankCount = 0; // 현재 순위와 동일한 질문의 수
        int accumulatedCount = 0; // 직전까지 지급한 포인트를 받은 사람 수

        for (int i = 0; i < topQuestions.size(); i++) {
            Question question = topQuestions.get(i);
            Member author = question.getAuthor();

            if (currentRecommendCount == question.getRecommendCount()) {
                assignedRankCount++;
            } else {
                accumulatedCount += assignedRankCount;
                currentRank += assignedRankCount;
                assignedRankCount = 1;
            }

            currentRecommendCount = question.getRecommendCount();

            // 순위가 3등 이내이고, 직전 순위가 3명을 넘지 않는 경우에만 해당 순위 포인트 지급
            if (accumulatedCount < 3 && currentRank <= 3) {
                int pointToAward = points[currentRank - 1]; // 순위에 맞는 포인트(공동 순위 고려)
                if (author != null) {
                    pointService.accumulate(author.getUsername(), pointToAward, AssetCategory.RANKING);
                }
                // 포인트 지급 후 랭킹 포인트 지급 여부 true로 변경
                question.setRankReceived(true);
            }

            // 3등 이후는 포인트 지급하지 않음
            if (currentRank > 3) {
                break;
            }
        }
    }
}
