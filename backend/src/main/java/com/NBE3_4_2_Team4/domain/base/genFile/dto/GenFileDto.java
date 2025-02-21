package com.NBE3_4_2_Team4.domain.base.genFile.dto;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class GenFileDto<T extends GenFile> {
    @NonNull
    private long id;
    @NonNull
    private LocalDateTime createdAt;
    @NonNull
    private long parentId;
    @NonNull
    private String fileName;
    @NonNull
    private GenFile.TypeCode typeCode;
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

    public GenFileDto(T genFile) {
        this.id = genFile.getId();
        this.createdAt = genFile.getCreatedAt();
        this.parentId = genFile.getParent().getId();
        this.fileName = genFile.getFileName();
        this.typeCode = genFile.getTypeCode();
        this.fileExtTypeCode = genFile.getFileExtTypeCode();
        this.fileExtType2Code = genFile.getFileExtType2Code();
        this.fileSize = genFile.getFileSize();
        this.fileNo = genFile.getFileNo();
        this.fileExt = genFile.getFileExt();
        this.fileDateDir = genFile.getFileDateDir();
        this.originalFileName = genFile.getOriginalFileName();
        this.downloadUrl = genFile.getDownloadUrl();
        this.publicUrl = genFile.getPublicUrl();
    }
}
