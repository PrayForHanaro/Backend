package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.domain.SavingsProduct;
import com.hanaro.prayerservice.dto.SavingsProductCreateRequest;
import com.hanaro.prayerservice.dto.SavingsProductResponse;
import com.hanaro.prayerservice.exception.PrayerErrorCode;
import com.hanaro.prayerservice.exception.PrayerException;
import com.hanaro.prayerservice.repository.SavingsProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsProductService {

    private final SavingsProductRepository repository;

    @Transactional
    public SavingsProductResponse create(SavingsProductCreateRequest request) {
        SavingsProduct product = SavingsProduct.builder()
                .name(request.getName())
                .interestRate(request.getInterestRate())
                .isActive(false)
                .build();
        return SavingsProductResponse.from(repository.save(product));
    }

    @Transactional(readOnly = true)
    public List<SavingsProductResponse> findAll() {
        return repository.findAll().stream()
                .map(SavingsProductResponse::from)
                .toList();
    }

    @Transactional
    public void activate(Long savingsProductId) {
        SavingsProduct target = repository.findById(savingsProductId)
                .orElseThrow(() -> new PrayerException(PrayerErrorCode.SAVINGS_PRODUCT_NOT_FOUND));
        repository.findByIsActiveTrue().ifPresent(SavingsProduct::deactivate);
        target.activate();
    }

    @Transactional
    public void delete(Long savingsProductId) {
        SavingsProduct target = repository.findById(savingsProductId)
                .orElseThrow(() -> new PrayerException(PrayerErrorCode.SAVINGS_PRODUCT_NOT_FOUND));
        repository.delete(target);
    }
}
