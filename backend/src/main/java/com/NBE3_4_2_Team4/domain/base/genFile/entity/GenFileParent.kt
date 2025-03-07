package com.NBE3_4_2_Team4.domain.base.genFile.entity

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import com.NBE3_4_2_Team4.standard.util.Ut
import jakarta.persistence.CascadeType
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToMany
import jakarta.persistence.Transient
import java.util.*
import java.util.stream.Collectors

@MappedSuperclass
abstract class GenFileParent<T : GenFile<P>, P : GenFileParent<T, P>>(
    @Transient
    private val type: Class<T>
) : BaseTime() {
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.REMOVE], orphanRemoval = true)
    val genFiles: MutableList<T> = mutableListOf()

    private fun createNewGenFileInstance(typeCode: GenFile.TypeCode, fileNo: Int): T {
        try {
            val newGenFile = type.getDeclaredConstructor().newInstance()

            (newGenFile as GenFile<P>).parent = this as P
            newGenFile.typeCode = typeCode
            newGenFile.fileNo = fileNo

            return newGenFile
        } catch (e: Exception) {
            throw RuntimeException("Failed to create new instance of ${type.name}", e)
        }
    }

    private fun processGenFile(oldGenFile: T?, typeCode: GenFile.TypeCode, fileNo: Int, filePath: String): T {
        var fileNo = fileNo
        val isModify = oldGenFile != null
        val originalFileName = Ut.file.getOriginalFileName(filePath)
        val fileExt = Ut.file.getFileExt(filePath)
        val fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt)
        val fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt)

        val metadataStr = Ut.file.getMetadata(filePath).entries.stream()
            .map { entry: Map.Entry<String, Any> -> "${entry.key}-${entry.value}" }
            .collect(Collectors.joining(";"))

        val fileName = if (isModify) Ut.file.withNewExt(oldGenFile!!.fileName, fileExt) else "${
            UUID.randomUUID()
                .toString()
        }.${fileExt}"
        val fileSize = Ut.file.getFileSize(filePath)
        fileNo = if (fileNo == 0) getNextGenFileNo(typeCode) else fileNo

        val genFile = if (isModify)
            oldGenFile!!
        else
            createNewGenFileInstance(typeCode, fileNo)

        genFile.originalFileName = originalFileName
        genFile.metadata = metadataStr
        genFile.fileDateDir = Ut.date.getCurrentDateFormatted("yyyy_MM_dd")
        genFile.fileExt = fileExt
        genFile.fileExtTypeCode = fileExtTypeCode
        genFile.fileExtType2Code = fileExtType2Code
        genFile.fileName = fileName
        genFile.fileSize = fileSize

        if (!isModify) genFiles.add(genFile)
        if (isModify) Ut.file.rm(genFile.filePath)

        Ut.file.mv(filePath, genFile.filePath)

        return genFile
    }

    fun addGenFile(typeCode: GenFile.TypeCode, filePath: String): T {
        return addGenFile(typeCode, 0, filePath)
    }

    private fun addGenFile(typeCode: GenFile.TypeCode, fileNo: Int, filePath: String): T {
        return processGenFile(null, typeCode, fileNo, filePath)
    }

    private fun getNextGenFileNo(typeCode: GenFile.TypeCode): Int {
        return genFiles.stream()
            .filter { genFile -> genFile.typeCode == typeCode }
            .mapToInt { genFile -> genFile.fileNo }
            .max()
            .orElse(0) + 1
    }

    fun getGenFileById(id: Long): T {
        return genFiles.stream()
            .filter { genFile: T -> genFile.id == id }
            .findFirst()
            .orElseThrow {
                ServiceException(
                    "404-2",
                    "%d번 파일은 존재하지 않습니다.".formatted(id)
                )
            }
    }

    fun getGenFileByTypeCodeAndFileNo(typeCode: GenFile.TypeCode, fileNo: Int): Optional<T> {
        return genFiles.stream()
            .filter { genFile: T -> genFile.typeCode == typeCode }
            .filter { genFile: T -> genFile.fileNo == fileNo }
            .findFirst()
    }

    fun deleteGenFile(typeCode: GenFile.TypeCode, fileNo: Int) {
        getGenFileByTypeCodeAndFileNo(typeCode, fileNo)
            .ifPresent { this.deleteGenFile(it) }
    }

    fun deleteGenFile(genFile: T) {
        Ut.file.rm(genFile.filePath)
        genFiles.remove(genFile)
    }

    fun modifyGenFile(genFile: T, filePath: String): T {
        return processGenFile(genFile, genFile.typeCode, genFile.fileNo, filePath)
    }

    fun modifyGenFile(typeCode: GenFile.TypeCode, fileNo: Int, filePath: String): T {
        val genFile = getGenFileByTypeCodeAndFileNo(
            typeCode,
            fileNo
        ).get()

        return modifyGenFile(genFile, filePath)
    }

    fun putGenFile(typeCode: GenFile.TypeCode, fileNo: Int, filePath: String): T {
        val opGenFile = getGenFileByTypeCodeAndFileNo(
            typeCode,
            fileNo
        )

        return if (opGenFile.isPresent) {
            modifyGenFile(typeCode, fileNo, filePath)
        } else {
            addGenFile(typeCode, fileNo, filePath)
        }
    }

    abstract fun checkActorCanMakeNewGenFile(actor: Member)

    protected abstract fun getCheckActorCanMakeNewGenFileRs(actor: Member): RsData<Empty>

    abstract fun modify(content: String)
    abstract val content: String
}
