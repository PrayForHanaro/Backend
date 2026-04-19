package com.hanaro.prayerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrayerMessageListResponse {

    private List<PrayerMessageResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private boolean hasNext;

    public static PrayerMessageListResponse from(Page<PrayerMessageResponse> page) {
        return PrayerMessageListResponse.builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }
}
