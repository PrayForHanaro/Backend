package com.hanaro.prayerservice.scheduler;

import com.hanaro.prayerservice.service.AutoTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 기도적금 자동이체 (BLESS_SPEC §6).
 * - 매일 01:00 (KST) 기준으로 오늘 자동이체 대상 Gift 처리
 * - 말일 보정: 오늘이 말일이면 transferDay가 월 길이 초과인 Gift도 함께 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoTransferScheduler {

    private final AutoTransferService autoTransferService;

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void run() {
        LocalDate today = LocalDate.now();
        log.info("[AutoTransferScheduler] 시작 date={}", today);
        autoTransferService.runFor(today);
        log.info("[AutoTransferScheduler] 종료 date={}", today);
    }
}
