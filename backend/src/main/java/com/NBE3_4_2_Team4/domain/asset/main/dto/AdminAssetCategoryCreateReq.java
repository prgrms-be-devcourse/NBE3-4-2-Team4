package com.NBE3_4_2_Team4.domain.asset.main.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAssetCategoryCreateReq {
    @NotBlank
    String name;
}
