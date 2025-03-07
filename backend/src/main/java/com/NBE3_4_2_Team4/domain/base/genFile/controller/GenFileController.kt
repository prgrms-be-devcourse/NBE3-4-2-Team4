package com.NBE3_4_2_Team4.domain.base.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile
import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent
import com.NBE3_4_2_Team4.standard.util.Ut
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.FileInputStream

@Controller
@Tag(name = "GenFileController", description = "파일 -  다운로드 등 다양한 기능 제공 API")
abstract class GenFileController<P : GenFileParent<G, P>, G : GenFile<P>>(
    protected val service: Any
) {
    @GetMapping("/download/{parentId}/{fileName}")
    @Operation(summary = "파일 다운로드")
    @Transactional
    fun download(
        @PathVariable parentId: Long,
        @PathVariable fileName: String,
        request: HttpServletRequest
    ): ResponseEntity<Resource> {
        val parent = findById(parentId)
        val genFile = getGenFileFromParent(parent, fileName)
        val filePath = genFile.filePath
        val resource: Resource = InputStreamResource(FileInputStream(filePath))
        var contentType = request.servletContext.getMimeType(filePath)

        if (contentType == null) contentType = "application/octet-stream"

        val downloadFileName = Ut.url.encode(genFile.originalFileName).replace("%20", " ")

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$downloadFileName\"")
            .contentType(MediaType.parseMediaType(contentType)).body(resource)
    }

    protected abstract fun findById(id: Long): P
    protected abstract fun getGenFileFromParent(parent: P, fileName: String): G
}
