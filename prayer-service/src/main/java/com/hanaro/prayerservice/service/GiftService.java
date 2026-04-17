package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.dto.GiftReceiverResponse;
import com.hanaro.prayerservice.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftService {
    private final GiftRepository giftRepository;

    public List<GiftReceiverResponse> getMyReceivers(Long userId) {
        return giftRepository.findAllBySenderIdAndIsActiveTrue(userId).stream()
                .map(g -> new GiftReceiverResponse(g.getReceiverId(), g.getGiftReceiverType().name()))
                .toList();
    }
}
