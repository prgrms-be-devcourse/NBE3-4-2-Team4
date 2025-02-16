package com.NBE3_4_2_Team4.domain.board.genFile.controller;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
import com.NBE3_4_2_Team4.standard.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Controller
@RequestMapping("/answer/genFile")
@RequiredArgsConstructor
@Tag(name = "AnswerGenFileController", description = "파일 다운로드 등 다양한 기능 제공")
public class AnswerGenFileController {
    private final AnswerService answerService;

    @GetMapping("/download/{answerId}/{fileName}")
    @Operation(summary = "파일 다운로드")
    @Transactional
    public ResponseEntity<Resource> download(
            @PathVariable long answerId,
            @PathVariable String fileName,
            HttpServletRequest request
    ) throws FileNotFoundException {
        Answer answer = answerService.findById(answerId);

        AnswerGenFile genFile = answer.getGenFiles()
                .stream()
                .filter(f -> f.getFileName().equals(fileName))
                .findFirst()
                .get();

        String filePath = genFile.getFilePath();

        Resource resource = new InputStreamResource(new FileInputStream(filePath));

        String contentType = request.getServletContext().getMimeType(filePath);

        if (contentType == null) contentType = "application/octet-stream";

        String downloadFileName = Ut.url.encode(genFile.getOriginalFileName()).replace("%20", " ");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                .contentType(MediaType.parseMediaType(contentType)).body(resource);
    }
}
