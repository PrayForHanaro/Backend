package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class GiftRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;

    @Autowired
    private GiftRepository repository;

    private final Gift newGift = Gift.builder()
        .senderId(1L)
        .receiverId(2L)
        .giftReceiverType(GiftReceiverType.SON)
        .fromAccountId(100L)
        .toSavingsAccountId(200L)
        .amount(new BigDecimal("50000.00"))
        .transferDay(10)
        .goalDays(365)
        .cumulativeTotal(BigDecimal.ZERO)
        .savingsProductId(1L)
        .savingsProductName("행복적금")
        .interestRate(new BigDecimal("3.5"))
        .build();

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
    }

    @Test
    @Order(1)
    void writeTest() {
        Gift saved = repository.save(newGift);
        assertThat(saved).usingRecursiveComparison()
            .ignoringFields("giftId", "createdAt", "updatedAt", "prayerSavings")
            .isEqualTo(newGift);
        id = saved.getGiftId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        Gift found = repository.findById(id).orElseThrow();
        assertThat(found.getReceiverId()).isEqualTo(newGift.getReceiverId());
        System.out.println("found receiverId = " + found.getReceiverId());
    }

    @Test
    @Order(3)
    void updateTest() {
        Gift found = repository.findById(id).orElseThrow();
        found.deactivate();
        repository.save(found);
        repository.flush();

        assertThat(repository.findById(id).get().isActive()).isFalse();
    }

    @Test
    @Order(4)
    void deleteTest() {
        repository.deleteById(id);
        repository.flush();
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @Order(5)
    void finalCheck() {
        assertThat(repository.count()).isEqualTo(orgCount);
        System.out.println("finalCheck count = " + repository.count());
    }
}