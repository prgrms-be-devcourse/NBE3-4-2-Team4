package com.NBE3_4_2_Team4.domain.base.genFile.entity;

import com.NBE3_4_2_Team4.global.config.AppConfig;
import com.NBE3_4_2_Team4.standard.util.Ut;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class GenFile {
    @Id
    @GeneratedValue(strategy = IDENTITY) // AUTO_INCREMENT
    @Setter(AccessLevel.PROTECTED)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    protected Long id;

    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    private int fileNo;
    private String originalFileName;
    private String metadata;
    private String fileDateDir;
    private String fileExt;
    private String fileExtTypeCode;
    private String fileExtType2Code;
    private String fileName;
    private int fileSize;

    public String getModelName() {
        String simpleName = this.getClass().getSimpleName();
        return Ut.str.lcfirst(simpleName);
    }

    public String getFilePath() {
        return AppConfig.getGenFileDirPath() + "/" + getModelName() + "/" + getTypeCodeAsStr() + "/" + fileDateDir + "/" + fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (getId() != null) return super.equals(o);

        if (o == null || getClass() != o.getClass()) return false;

        if (!super.equals(o)) return false;

        GenFile that = (GenFile) o;
        return fileNo == that.getFileNo() && Objects.equals(getTypeCodeAsStr(), that.getTypeCodeAsStr());
    }

    @Override
    public int hashCode() {
        if (getId() != null) return super.hashCode();

        return Objects.hash(super.hashCode(), getTypeCodeAsStr(), fileNo);
    }

    private String getOwnerModelName() {
        return this.getModelName().replace("GenFile", "");
    }

    public String getDownloadUrl() {
        return AppConfig.getSiteBackUrl() + "/" + getOwnerModelName() + "/genFile/download/" + getOwnerModelId() + "/" + fileName;
    }

    public String getPublicUrl() {
        return AppConfig.getSiteBackUrl() + "/gen/" + getModelName() + "/" + getTypeCodeAsStr() + "/" + fileDateDir + "/" + fileName;
    }

    abstract protected long getOwnerModelId();
    abstract protected String getTypeCodeAsStr();
}
