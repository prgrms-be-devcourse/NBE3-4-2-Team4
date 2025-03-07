package com.NBE3_4_2_Team4.domain.base.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.dto.GenFileDto
import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile
import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent
import com.NBE3_4_2_Team4.global.config.AppConfig
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.standard.base.Empty
import com.NBE3_4_2_Team4.standard.util.Ut
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.lang.NonNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@Tag(name = "ApiGenFileController", description = "파일 -  파일 컨트롤 API")
abstract class ApiGenFileController<P : GenFileParent<G, P>, G : GenFile<P>>(
    protected val service: Any
) {
    @Autowired
    private lateinit var entityManager: EntityManager

    @PostMapping(value = ["/{typeCode}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "다건등록", description = "파일을 한꺼번에 여러개 업로드 합니다.")
    @Transactional
    fun makeNewItems(
        @PathVariable parentId: Long,
        @PathVariable typeCode: GenFile.TypeCode,
        @NonNull @RequestPart("files") files: Array<MultipartFile>
    ): RsData<List<GenFileDto<G>>> {
        val actor = AuthManager.getNonNullMember()
        val parent = findById(parentId)

        parent.checkActorCanMakeNewGenFile(actor)

        val genFiles: MutableList<G> = ArrayList()
        val newSrcs: MutableList<String> = ArrayList()

        for (file in files) {
            if (file.isEmpty) continue

            val filePath = Ut.file.toFile(file, AppConfig.getTempDirPath())

            val genFile = parent.addGenFile(
                typeCode,
                filePath
            )

            genFiles.add(genFile)

            if (typeCode == GenFile.TypeCode.body) {
                newSrcs.add(genFile.publicUrl)
            }
        }

        entityManager.flush()

        if (typeCode == GenFile.TypeCode.body && newSrcs.isNotEmpty()) {
            parent.modify(Ut.editorImg.updateImgSrc(parent.content, newSrcs))
        }

        return RsData(
            "201-1",
            "${genFiles.size}개의 파일이 생성되었습니다.",
            genFiles.stream().map { GenFileDto(it) }.toList()
        )
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건조회", description = "해당 게시글의 파일 목록을 조회합니다.")
    fun items(
        @PathVariable parentId: Long
    ): List<GenFileDto<G>> {
        val parent = findById(parentId)

        return parent
            .genFiles
            .stream()
            .map { GenFileDto(it) }
            .toList()
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건조회", description = "id를 기준으로 특정 파일 정보를 가져옵니다.")
    fun item(
        @PathVariable parentId: Long,
        @PathVariable id: Long
    ): GenFileDto<G> {
        val parent = findById(parentId)

        val genFile = parent.getGenFileById(id)

        return GenFileDto(genFile)
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제", description = "등록된 파일을 삭제합니다.")
    fun delete(
        @PathVariable parentId: Long,
        @PathVariable id: Long
    ): RsData<Empty> {
        val parent = findById(parentId)
        val genFile = parent.getGenFileById(id)

        parent.deleteGenFile(genFile)

        return RsData(
            "200-1",
            "${id}번 파일이 삭제되었습니다."
        )
    }

    @PutMapping(value = ["/{id}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Transactional
    @Operation(summary = "수정", description = "등록된 파일을 수정합니다.")
    fun modify(
        @PathVariable parentId: Long,
        @PathVariable id: Long,
        @NonNull @RequestPart("file") file: MultipartFile?
    ): RsData<GenFileDto<G>> {
        val parent = findById(parentId)
        val genFile = parent.getGenFileById(id)
        val filePath = Ut.file.toFile(file, AppConfig.getTempDirPath())

        parent.modifyGenFile(genFile, filePath)

        return RsData(
            "200-1",
            "${id}번 파일이 수정되었습니다.",
            GenFileDto(genFile)
        )
    }

    protected abstract fun findById(id: Long): P
}
