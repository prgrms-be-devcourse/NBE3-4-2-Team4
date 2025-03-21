package com.NBE3_4_2_Team4.domain.asset.main.dto;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminAssetCategoryRes {

    private Long id;

    private String name;

    public static AdminAssetCategoryRes from(AdminAssetCategory entity) {
        return AdminAssetCategoryRes
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
