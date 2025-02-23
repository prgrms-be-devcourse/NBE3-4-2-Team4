package com.NBE3_4_2_Team4.domain.base.genFile.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.util.Ut;
import jakarta.persistence.CascadeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class GenFileParent<T extends GenFile> extends BaseTime {
    private Class<T> type;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<T> genFiles = new ArrayList<>();

    public GenFileParent(Class<T> type) {
        this.type = type;
    }

    private T createNewGenFileInstance(GenFile.TypeCode typeCode, int fileNo) {
        try {
            T newGenFile = type.getDeclaredConstructor().newInstance();

            newGenFile.setParent(this);
            newGenFile.setTypeCode(typeCode);
            newGenFile.setFileNo(fileNo);

            return newGenFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new instance of " + type.getName(), e);
        }
    }

    private T processGenFile(T oldGenFile, GenFile.TypeCode typeCode, int fileNo, String filePath) {
        boolean isModify = oldGenFile != null;
        String originalFileName = Ut.file.getOriginalFileName(filePath);
        String fileExt = Ut.file.getFileExt(filePath);
        String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
        String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);

        String metadataStr = Ut.file.getMetadata(filePath).entrySet().stream()
                .map(entry -> entry.getKey() + "-" + entry.getValue())
                .collect(Collectors.joining(";"));

        String fileName = isModify ? Ut.file.withNewExt(oldGenFile.getFileName(), fileExt) : UUID.randomUUID() + "." + fileExt;
        int fileSize = Ut.file.getFileSize(filePath);
        fileNo = fileNo == 0 ? getNextGenFileNo(typeCode) : fileNo;

        T genFile = isModify
                ? oldGenFile
                : createNewGenFileInstance(typeCode, fileNo);

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

    public T addGenFile(GenFile.TypeCode typeCode, String filePath) {
        return addGenFile(typeCode, 0, filePath);
    }

    private T addGenFile(GenFile.TypeCode typeCode, int fileNo, String filePath) {
        return processGenFile(null, typeCode, fileNo, filePath);
    }

    private int getNextGenFileNo(GenFile.TypeCode typeCode) {
        return genFiles.stream()
                .filter(genFile -> genFile.getTypeCode().equals(typeCode))
                .mapToInt(T::getFileNo)
                .max()
                .orElse(0) + 1;
    }

    public T getGenFileById(long id) {
        return genFiles.stream()
                .filter(genFile -> genFile.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new ServiceException("404-2", "%d번 파일은 존재하지 않습니다.".formatted(id))
                );
    }

    public Optional<T> getGenFileByTypeCodeAndFileNo(GenFile.TypeCode typeCode, int fileNo) {
        return genFiles.stream()
                .filter(genFile -> genFile.getTypeCode().equals(typeCode))
                .filter(genFile -> genFile.getFileNo() == fileNo)
                .findFirst();
    }

    public void deleteGenFile(GenFile.TypeCode typeCode, int fileNo) {
        getGenFileByTypeCodeAndFileNo(typeCode, fileNo)
                .ifPresent(this::deleteGenFile);
    }

    public void deleteGenFile(T genFile) {
        Ut.file.rm(genFile.getFilePath());
        genFiles.remove(genFile);
    }

    public void modifyGenFile(T genFile, String filePath) {
        processGenFile(genFile, genFile.getTypeCode(), genFile.getFileNo(), filePath);
    }

    public void modifyGenFile(GenFile.TypeCode typeCode, int fileNo, String filePath) {
        getGenFileByTypeCodeAndFileNo(
                typeCode,
                fileNo
        ).ifPresent(postGenFile -> modifyGenFile(postGenFile, filePath));
    }

    public void putGenFile(GenFile.TypeCode typeCode, int fileNo, String filePath) {
        Optional<T> opGenFile = getGenFileByTypeCodeAndFileNo(
                typeCode,
                fileNo
        );

        if (opGenFile.isPresent()) {
            modifyGenFile(typeCode, fileNo, filePath);
        } else {
            addGenFile(typeCode, fileNo, filePath);
        }
    }

    public abstract void checkActorCanMakeNewGenFile(Member actor);

    protected abstract RsData<Empty> getCheckActorCanMakeNewGenFileRs(Member actor);

    public abstract void modify(String content);
    public abstract String getContent();
}
