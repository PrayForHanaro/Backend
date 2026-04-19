package com.hanaro.prayerservice.event;

import com.hanaro.prayerservice.domain.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointEarnEvent {

    private Long userId;
    private PointType pointType;
    private Long amount;
    private Long refId;
    private String info;
    private Instant timestamp;
}
