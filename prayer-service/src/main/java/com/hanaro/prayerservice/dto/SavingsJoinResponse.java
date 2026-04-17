package com.hanaro.prayerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsJoinResponse {

    private Long giftId;
    private LocalDate startDate;
    private Long earnedPoint;
}
