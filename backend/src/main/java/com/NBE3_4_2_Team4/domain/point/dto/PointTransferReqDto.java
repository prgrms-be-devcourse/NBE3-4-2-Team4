package com.NBE3_4_2_Team4.domain.point.dto;

import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
public class PointTransferReqDto {
    @NotBlank(message = "username은 필수 입력 값입니다.")
    private String username;

    @NotNull(message = "amount는 필수 입력 값입니다.")
    @Min(value = 1, message = "송금 금액은 1 이상이어야 합니다.")
    private Long amount;
}


