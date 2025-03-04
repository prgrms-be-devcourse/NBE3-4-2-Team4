package com.NBE3_4_2_Team4.domain.base.genFile.controller;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile;
import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent;
import com.NBE3_4_2_Team4.standard.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Controller
@Tag(name = "GenFileController", description = "파일 다운로드 등 다양한 기능 제공")
public abstract class GenFileController<P extends GenFileParent, G extends GenFile> {
    protected final Object service;

    protected GenFileController(Object service) {
        this.service = service;
    }

    @GetMapping("/download/{parentId}/{fileName}")
    @Operation(summary = "파일 다운로드")
    @Transactional
    public ResponseEntity<Resource> download(
            @PathVariable long parentId,
            @PathVariable String fileName,
            HttpServletRequest request
    ) throws FileNotFoundException {
        P parent = findById(parentId);

        G genFile = getGenFileFromParent(parent, fileName);

        String filePath = genFile.getFilePath();

        Resource resource = new InputStreamResource(new FileInputStream(filePath));

        String contentType = request.getServletContext().getMimeType(filePath);

        if (contentType == null) contentType = "application/octet-stream";

        String downloadFileName = Ut.url.encode(genFile.getOriginalFileName()).replace("%20", " ");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                .contentType(MediaType.parseMediaType(contentType)).body(resource);
    }

    protected abstract P findById(long id);
    protected abstract G getGenFileFromParent(P parent, String fileName);
}
