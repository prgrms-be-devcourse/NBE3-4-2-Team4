package com.NBE3_4_2_Team4.domain.base.genFile.controller;


import com.NBE3_4_2_Team4.domain.base.genFile.dto.GenFileDto;
import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile;
import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.config.AppConfig;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@Tag(name = "ApiGenFileController", description = "API 파일 컨트롤러")
public abstract class ApiGenFileController<P extends GenFileParent, G extends GenFile<P>>  {
    protected final Object service;
    @Autowired
    private EntityManager entityManager;

    protected ApiGenFileController(Object service) {
        this.service = service;
    }

    @PostMapping(value = "/{typeCode}", consumes = MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다건등록")
    @Transactional
    public RsData<List<GenFileDto<G>>> makeNewItems(
            @PathVariable long parentId,
            @PathVariable GenFile.TypeCode typeCode,
            @NonNull @RequestPart("files") MultipartFile[] files
    ) {
        Member actor = AuthManager.getNonNullMember();
        P parent = findById(parentId);

        parent.checkActorCanMakeNewGenFile(actor);

        List<G> genFiles = new ArrayList<>();
        List<String> newSrcs = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String filePath = Ut.file.toFile(file, AppConfig.getTempDirPath());
            G genFile = (G) parent.addGenFile(
                    typeCode,
                    filePath
            );

            genFiles.add(genFile);

            if(typeCode == GenFile.TypeCode.body) {
                newSrcs.add(genFile.getPublicUrl());
            }
        }

        entityManager.flush();

        if(typeCode == GenFile.TypeCode.body && !newSrcs.isEmpty()) {
            parent.modify(Ut.editorImg.updateImgSrc(parent.getContent(), newSrcs));
        }

        return new RsData<>(
                "201-1",
                "%d개의 파일이 생성되었습니다.".formatted(genFiles.size()),
                genFiles.stream().map(GenFileDto::new).toList()
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건조회")
    public List<GenFileDto<G>> items(
            @PathVariable long parentId
    ) {
        P parent = findById(parentId);

        return parent
                .getGenFiles()
                .stream()
                .map(genFile -> new GenFileDto<G>((G) genFile))
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건조회")
    public GenFileDto item(
            @PathVariable long parentId,
            @PathVariable long id
    ) {
        P parent = findById(parentId);

        G genFile = (G) parent.getGenFileById(id);

        return new GenFileDto(genFile);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    public RsData<Empty> delete(
            @PathVariable long parentId,
            @PathVariable long id
    ) {
        P parent = findById(parentId);

        G genFile = (G) parent.getGenFileById(id);

        parent.deleteGenFile(genFile);

        return new RsData<>(
                "200-1",
                "%d번 파일이 삭제되었습니다.".formatted(id)
        );
    }

    @PutMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @Operation(summary = "수정")
    public RsData<GenFileDto> modify(
            @PathVariable long parentId,
            @PathVariable long id,
            @NonNull @RequestPart("file") MultipartFile file
    ) {
        P parent = findById(parentId);

        G genFile = (G) parent.getGenFileById(id);

        String filePath = Ut.file.toFile(file, AppConfig.getTempDirPath());

        parent.modifyGenFile(genFile, filePath);

        return new RsData<>(
                "200-1",
                "%d번 파일이 수정되었습니다.".formatted(id),
                new GenFileDto(genFile)
        );
    }

    protected abstract P findById(long id);
}
