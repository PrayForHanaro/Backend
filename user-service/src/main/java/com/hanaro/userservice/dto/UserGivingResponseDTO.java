package com.hanaro.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGivingResponseDTO {
    private String name;
    private BigDecimal maxPoint;
    private String bankAccount;
    private Long orgId;
    private Long accountId;
    private Double donationRate;
}
