package com.NBE3_4_2_Team4.domain.base.genFile.dto;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile
import java.time.LocalDateTime

class GenFileDto<T : GenFile<*>>(
    val id: Long,
    val createdAt: LocalDateTime,
    val parentId: Long,
    val fileName: String,
    val typeCode: GenFile.TypeCode,
    val fileExtTypeCode: String,
    val fileExtType2Code: String,
    val fileSize: Int,
    val fileNo: Int,
    val fileExt: String,
    val fileDateDir: String,
    val originalFileName: String,
    val downloadUrl: String,
    val publicUrl: String,
    val fileType: String
) {
    constructor(genFile: T) : this(
        id = genFile.id,
        createdAt = genFile.createdAt,
        parentId = genFile.parent.id,
        fileName = genFile.fileName,
        typeCode = genFile.typeCode,
        fileExtTypeCode = genFile.fileExtTypeCode,
        fileExtType2Code = genFile.fileExtType2Code,
        fileSize = genFile.fileSize,
        fileNo = genFile.fileNo,
        fileExt = genFile.fileExt,
        fileDateDir = genFile.fileDateDir,
        originalFileName = genFile.originalFileName,
        downloadUrl = genFile.downloadUrl,
        publicUrl = genFile.publicUrl,
        fileType = genFile.modelName.substring(0, genFile.modelName.indexOf("GenFile"))
    )
}
