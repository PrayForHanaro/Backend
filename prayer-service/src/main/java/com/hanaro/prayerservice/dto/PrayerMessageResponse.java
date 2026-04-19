package com.hanaro.prayerservice.dto;

import com.hanaro.prayerservice.domain.PrayerSavings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrayerMessageResponse {

    private Long messageId;
    private String content;
    private LocalDate startDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PrayerMessageResponse from(PrayerSavings ps) {
        return PrayerMessageResponse.builder()
                .messageId(ps.getPrayerSavingsId())
                .content(ps.getPrayerContent())
                .startDate(ps.getStartDate())
                .createdAt(ps.getCreatedAt())
                .updatedAt(ps.getUpdatedAt())
                .build();
    }
}
