package com.NBE3_4_2_Team4.domain.board.genFile.controller;


import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.base.genFile.dto.GenFileDto;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.config.AppConfig;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/answers/{answerId}/genFiles")
@RequiredArgsConstructor
@Tag(name = "ApiAnswerGenFileController", description = "API 답변 파일 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class ApiAnswerGenFileController {
    private final AnswerService answerService;

    @PostMapping(value = "/{typeCode}", consumes = MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다건등록")
    @Transactional
    public RsData<List<GenFileDto<AnswerGenFile>>> makeNewItems(
            @PathVariable long answerId,
            @PathVariable GenFile.TypeCode typeCode,
            @NonNull @RequestPart("files") MultipartFile[] files
    ) {
        Member actor = AuthManager.getNonNullMember();
        Answer answer = answerService.findById(answerId);

        answer.checkActorCanMakeNewGenFile(actor);

        List<AnswerGenFile> answerGenFiles = new ArrayList<>();
        List<String> newSrcs = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String filePath = Ut.file.toFile(file, AppConfig.getTempDirPath());
            AnswerGenFile answerGenFile = answer.addGenFile(
                    typeCode,
                    filePath
            );

            answerGenFiles.add(answerGenFile);

            if(typeCode == GenFile.TypeCode.body) {
                newSrcs.add(answerGenFile.getPublicUrl());
            }
        }

        if(typeCode == GenFile.TypeCode.body && !newSrcs.isEmpty()) {
            answer.modify(Ut.editorImg.updateImgSrc(answer.getContent(), newSrcs));
        }

        answerService.flush();

        return new RsData<>(
                "201-1",
                "%d개의 파일이 생성되었습니다.".formatted(answerGenFiles.size()),
                answerGenFiles.stream().map(GenFileDto::new).toList()
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건조회")
    public List<GenFileDto<AnswerGenFile>> items(
            @PathVariable long answerId
    ) {
        Answer answer = answerService.findById(answerId);

        return answer
                .getGenFiles()
                .stream()
                .map(GenFileDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건조회")
    public GenFileDto item(
            @PathVariable long answerId,
            @PathVariable long id
    ) {
        Answer answer = answerService.findById(answerId);

        AnswerGenFile answerGenFile = answer.getGenFileById(id);

        return new GenFileDto(answerGenFile);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    public RsData<Empty> delete(
            @PathVariable long answerId,
            @PathVariable long id
    ) {
        Answer answer = answerService.findById(answerId);

        AnswerGenFile answerGenFile = answer.getGenFileById(id);

        answer.deleteGenFile(answerGenFile);

        return new RsData<>(
                "200-1",
                "%d번 파일이 삭제되었습니다.".formatted(id)
        );
    }

    @PutMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @Operation(summary = "수정")
    public RsData<GenFileDto> modify(
            @PathVariable long answerId,
            @PathVariable long id,
            @NonNull @RequestPart("file") MultipartFile file
    ) {
        Answer answer = answerService.findById(answerId);

        AnswerGenFile answerGenFile = answer.getGenFileById(id);

        String filePath = Ut.file.toFile(file, AppConfig.getTempDirPath());

        answer.modifyGenFile(answerGenFile, filePath);

        return new RsData<>(
                "200-1",
                "%d번 파일이 수정되었습니다.".formatted(id),
                new GenFileDto(answerGenFile)
        );
    }
}
