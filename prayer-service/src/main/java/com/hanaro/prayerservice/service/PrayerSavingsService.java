package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.domain.Gift;
import com.hanaro.prayerservice.domain.PrayerSavings;
import com.hanaro.prayerservice.dto.PrayerMessageCreateRequest;
import com.hanaro.prayerservice.dto.PrayerMessageDeleteResponse;
import com.hanaro.prayerservice.dto.PrayerMessageListResponse;
import com.hanaro.prayerservice.dto.PrayerMessageResponse;
import com.hanaro.prayerservice.dto.PrayerMessageUpdateRequest;
import com.hanaro.prayerservice.exception.PrayerErrorCode;
import com.hanaro.prayerservice.exception.PrayerException;
import com.hanaro.prayerservice.repository.GiftRepository;
import com.hanaro.prayerservice.repository.PrayerSavingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PrayerSavingsService {

    private static final int MAX_CONTENT_LENGTH = 250;

    private final GiftRepository giftRepository;
    private final PrayerSavingsRepository prayerSavingsRepository;

    @Transactional
    public PrayerMessageResponse create(Long senderId, Long giftId, PrayerMessageCreateRequest request) {
        String content = sanitizeContent(request.getContent());
        Gift gift = loadOwnedGift(giftId, senderId);

        PrayerSavings saved = prayerSavingsRepository.save(PrayerSavings.builder()
                .gift(gift)
                .prayerContent(content)
                .startDate(LocalDate.now())
                .build());

        return PrayerMessageResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PrayerMessageListResponse list(Long senderId, Long giftId, int page, int size) {
        loadOwnedGift(giftId, senderId);

        Pageable pageable = PageRequest.of(page, size);
        return PrayerMessageListResponse.from(
                prayerSavingsRepository.findByGiftId(giftId, pageable).map(PrayerMessageResponse::from)
        );
    }

    @Transactional
    public PrayerMessageResponse update(Long senderId, Long messageId, PrayerMessageUpdateRequest request) {
        String content = sanitizeContent(request.getContent());
        PrayerSavings message = prayerSavingsRepository.findByIdAndSenderId(messageId, senderId)
                .orElseThrow(() -> new PrayerException(PrayerErrorCode.MESSAGE_NOT_FOUND));

        message.updateContent(content);
        return PrayerMessageResponse.from(message);
    }

    @Transactional
    public PrayerMessageDeleteResponse delete(Long senderId, Long messageId) {
        PrayerSavings message = prayerSavingsRepository.findByIdAndSenderId(messageId, senderId)
                .orElseThrow(() -> new PrayerException(PrayerErrorCode.MESSAGE_NOT_FOUND));

        prayerSavingsRepository.delete(message);
        return PrayerMessageDeleteResponse.builder()
                .messageId(messageId)
                .deleted(true)
                .build();
    }

    private Gift loadOwnedGift(Long giftId, Long senderId) {
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new PrayerException(PrayerErrorCode.GIFT_NOT_FOUND));
        if (!gift.getSenderId().equals(senderId)) {
            throw new PrayerException(PrayerErrorCode.GIFT_NOT_OWNED);
        }
        return gift;
    }

    private String sanitizeContent(String raw) {
        if (raw == null) {
            throw new PrayerException(PrayerErrorCode.MESSAGE_CONTENT_INVALID);
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty() || trimmed.length() > MAX_CONTENT_LENGTH) {
            throw new PrayerException(PrayerErrorCode.MESSAGE_CONTENT_INVALID);
        }
        return trimmed;
    }
}
