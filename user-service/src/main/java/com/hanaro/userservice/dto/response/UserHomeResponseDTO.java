package com.hanaro.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHomeResponseDTO {
    private String userName;
    private int myPoint;
    private Long orgId;
    private Double donationRate;
}
