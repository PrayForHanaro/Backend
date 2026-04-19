package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.domain.Gift;
import com.hanaro.prayerservice.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTransferService {

    private final GiftRepository giftRepository;
    private final AutoTransferProcessor autoTransferProcessor;

    public void runFor(LocalDate date) {
        int day = date.getDayOfMonth();
        boolean isLastDay = date.lengthOfMonth() == day;

        List<Gift> due = giftRepository.findDueGifts(day, isLastDay);
        log.info("[AutoTransfer] {} 대상 Gift {}건 (isLastDay={})", date, due.size(), isLastDay);

        for (Gift gift : due) {
            autoTransferProcessor.processOne(gift.getGiftId(), date);
        }
    }
}
