package com.NBE3_4_2_Team4.domain.board.genFile.dto;

import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class AnswerGenFileDto {
    @NonNull
    private long id;
    @NonNull
    private LocalDateTime createdAt;
    @NonNull
    private long answerId;
    @NonNull
    private String fileName;
    @NonNull
    private AnswerGenFile.TypeCode typeCode;
    @NonNull
    private String fileExtTypeCode;
    @NonNull
    private String fileExtType2Code;
    @NonNull
    private long fileSize;
    @NonNull
    private long fileNo;
    @NonNull
    private String fileExt;
    @NonNull
    private String fileDateDir;
    @NonNull
    private String originalFileName;
    @NonNull
    private String downloadUrl;
    @NonNull
    private String publicUrl;

    public AnswerGenFileDto(AnswerGenFile answerGenFile) {
        this.id = answerGenFile.getId();
        this.createdAt = answerGenFile.getCreatedAt();
        this.answerId = answerGenFile.getAnswer().getId();
        this.fileName = answerGenFile.getFileName();
        this.typeCode = answerGenFile.getTypeCode();
        this.fileExtTypeCode = answerGenFile.getFileExtTypeCode();
        this.fileExtType2Code = answerGenFile.getFileExtType2Code();
        this.fileSize = answerGenFile.getFileSize();
        this.fileNo = answerGenFile.getFileNo();
        this.fileExt = answerGenFile.getFileExt();
        this.fileDateDir = answerGenFile.getFileDateDir();
        this.originalFileName = answerGenFile.getOriginalFileName();
        this.downloadUrl = answerGenFile.getDownloadUrl();
        this.publicUrl = answerGenFile.getPublicUrl();
    }

}
