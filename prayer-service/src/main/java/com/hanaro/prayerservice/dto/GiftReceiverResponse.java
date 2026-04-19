package com.hanaro.prayerservice.dto;

import java.math.BigDecimal;

/**
 * 내 기도적금 대상자 목록 한 건.
 * BLESS_SPEC §2-6/§2-6a/§2-6b/§2-6c 에 따라 금액/누적/기도일수 포함.
 * 대상자 이름·imageType 은 user-service 와의 BFF 조합 단계에서 추가됨.
 */
public record GiftReceiverResponse(
        Long giftId,
        Long receiverId,
        String relation,
        BigDecimal monthlyAmount,
        BigDecimal cumulativeTotal,
        int daysOfPrayer
) {}
