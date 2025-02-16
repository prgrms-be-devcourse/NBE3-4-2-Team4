package com.NBE3_4_2_Team4.domain.board.answer.entity;

import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.util.Ut;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean selected;

    private LocalDateTime selectedAt;

    @OneToMany(mappedBy = "answer", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<AnswerGenFile> genFiles = new ArrayList<>();

    public void checkActorCanModify(Member actor) {
        if (!actor.equals(this.getAuthor()))
            throw new ServiceException("403-2", "작성자만 답변을 수정할 수 있습니다.");
    }

    public void checkActorCanDelete(Member actor) {
        if (actor.getRole() != Member.Role.ADMIN && !actor.equals(this.getAuthor()))
            throw new ServiceException("403-2", "작성자만 답변을 삭제할 수 있습니다.");
    }

    public void modify(String content) {
        this.content = content;
    }

    public void select() {
        this.selected = true;
        this.setSelectedAt();
    }

    public void setSelectedAt() {
        this.selectedAt = LocalDateTime.now();
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    private AnswerGenFile processGenFile(AnswerGenFile oldAnswerGenFile, AnswerGenFile.TypeCode typeCode, int fileNo, String filePath) {
        boolean isModify = oldAnswerGenFile != null;
        String originalFileName = Ut.file.getOriginalFileName(filePath);
        String fileExt = Ut.file.getFileExt(filePath);
        String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
        String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);

        String metadataStr = Ut.file.getMetadata(filePath).entrySet().stream()
                .map(entry -> entry.getKey() + "-" + entry.getValue())
                .collect(Collectors.joining(";"));

        String fileName = isModify ? Ut.file.withNewExt(oldAnswerGenFile.getFileName(), fileExt) : UUID.randomUUID() + "." + fileExt;
        int fileSize = Ut.file.getFileSize(filePath);
        fileNo = fileNo == 0 ? getNextGenFileNo(typeCode) : fileNo;

        AnswerGenFile genFile = isModify ? oldAnswerGenFile : AnswerGenFile
                .builder()
                .answer(this)
                .typeCode(typeCode)
                .fileNo(fileNo)
                .build();

        genFile.setOriginalFileName(originalFileName);
        genFile.setMetadata(metadataStr);
        genFile.setFileDateDir(Ut.date.getCurrentDateFormatted("yyyy_MM_dd"));
        genFile.setFileExt(fileExt);
        genFile.setFileExtTypeCode(fileExtTypeCode);
        genFile.setFileExtType2Code(fileExtType2Code);
        genFile.setFileName(fileName);
        genFile.setFileSize(fileSize);

        if (!isModify) genFiles.add(genFile);

        if (isModify) {
            Ut.file.rm(genFile.getFilePath());
        }

        Ut.file.mv(filePath, genFile.getFilePath());

        return genFile;
    }

    public AnswerGenFile addGenFile(AnswerGenFile.TypeCode typeCode, String filePath) {
        return addGenFile(typeCode, 0, filePath);
    }

    private AnswerGenFile addGenFile(AnswerGenFile.TypeCode typeCode, int fileNo, String filePath) {
        return processGenFile(null, typeCode, fileNo, filePath);
    }

    private int getNextGenFileNo(AnswerGenFile.TypeCode typeCode) {
        return genFiles.stream()
                .filter(genFile -> genFile.getTypeCode().equals(typeCode))
                .mapToInt(AnswerGenFile::getFileNo)
                .max()
                .orElse(0) + 1;
    }

    public AnswerGenFile getGenFileById(long id) {
        return genFiles.stream()
                .filter(genFile -> genFile.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new ServiceException("404-2", "%d번 파일은 존재하지 않습니다.".formatted(id))
                );
    }

    public Optional<AnswerGenFile> getGenFileByTypeCodeAndFileNo(AnswerGenFile.TypeCode typeCode, int fileNo) {
        return genFiles.stream()
                .filter(genFile -> genFile.getTypeCode().equals(typeCode))
                .filter(genFile -> genFile.getFileNo() == fileNo)
                .findFirst();
    }

    public void deleteGenFile(AnswerGenFile.TypeCode typeCode, int fileNo) {
        getGenFileByTypeCodeAndFileNo(typeCode, fileNo)
                .ifPresent(this::deleteGenFile);
    }

    public void deleteGenFile(AnswerGenFile answerGenFile) {
        Ut.file.rm(answerGenFile.getFilePath());
        genFiles.remove(answerGenFile);
    }

    public void modifyGenFile(AnswerGenFile postGenFile, String filePath) {
        processGenFile(postGenFile, postGenFile.getTypeCode(), postGenFile.getFileNo(), filePath);
    }

    public void modifyGenFile(AnswerGenFile.TypeCode typeCode, int fileNo, String filePath) {
        getGenFileByTypeCodeAndFileNo(
                typeCode,
                fileNo
        ).ifPresent(postGenFile -> modifyGenFile(postGenFile, filePath));
    }

    public void putGenFile(AnswerGenFile.TypeCode typeCode, int fileNo, String filePath) {
        Optional<AnswerGenFile> opAnswerGenFile = getGenFileByTypeCodeAndFileNo(
                typeCode,
                fileNo
        );

        if (opAnswerGenFile.isPresent()) {
            modifyGenFile(typeCode, fileNo, filePath);
        } else {
            addGenFile(typeCode, fileNo, filePath);
        }
    }

    public void checkActorCanMakeNewGenFile(Member actor) {
        Optional.of(
                        getCheckActorCanMakeNewGenFileRs(actor)
                )
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new ServiceException(rsData.getResultCode(), rsData.getMsg());
                });
    }

    public RsData<Empty> getCheckActorCanMakeNewGenFileRs(Member actor) {
        if (actor == null) return new RsData<>("401-1", "로그인 후 이용해주세요.");
        if (actor.equals(author)) return RsData.OK;
        return new RsData<>("403-1", "작성자만 파일을 업로드할 수 있습니다.");
    }
}
