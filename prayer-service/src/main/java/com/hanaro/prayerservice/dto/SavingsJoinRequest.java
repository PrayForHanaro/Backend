package com.hanaro.prayerservice.dto;

import com.hanaro.prayerservice.domain.GiftReceiverType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsJoinRequest {

    private Long receiverId;
    private GiftReceiverType giftReceiverType;
    private BigDecimal amount;
    private int transferDay;
    private int goalDays;
}
