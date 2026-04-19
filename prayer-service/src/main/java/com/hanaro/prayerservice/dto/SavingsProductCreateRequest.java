package com.hanaro.prayerservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsProductCreateRequest {

    @NotBlank(message = "name은 필수입니다")
    @Size(max = 100, message = "name은 100자 이하여야 합니다")
    private String name;

    @NotNull(message = "interestRate는 필수입니다")
    @DecimalMin(value = "0.0", message = "interestRate는 0 이상이어야 합니다")
    private BigDecimal interestRate;
}
