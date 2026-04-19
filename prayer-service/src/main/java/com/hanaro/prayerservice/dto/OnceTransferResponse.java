package com.hanaro.prayerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnceTransferResponse {

    private String id;
    private Instant sentAt;
    private String status;
}
