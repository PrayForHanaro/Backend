package com.hanaro.offeringservice.repository;

import com.hanaro.offeringservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class OfferingRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;

    @Autowired
    private OfferingRepository repository;

    private final Offering newOffering = Offering.builder()
        .userId(1L)
        .orgId(1L)
        .accountId(100L)
        .offeringType(OfferingType.십일조)
        .amount(new BigDecimal("10000.00"))
        .offererName("홍길동")
        .prayerContent("가족 건강")
        .usedPoint(BigDecimal.ZERO)
        .build();

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
    }

    @Test
    @Order(1)
    void writeTest() {
        Offering saved = repository.save(newOffering);
        assertThat(saved).usingRecursiveComparison()
            .ignoringFields("offeringId", "createdAt", "updatedAt")
            .isEqualTo(newOffering);
        id = saved.getOfferingId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        Offering found = repository.findById(id).orElseThrow();
        assertThat(found.getOffererName()).isEqualTo(newOffering.getOffererName());
        System.out.println("found = " + found.getOffererName());
    }

    @Test
    @Order(3)
    void updateTest() {
        Offering found = repository.findById(id).orElseThrow();
        Offering updated = Offering.builder()
            .offeringId(found.getOfferingId())
            .userId(found.getUserId())
            .orgId(found.getOrgId())
            .accountId(found.getAccountId())
            .offeringType(found.getOfferingType())
            .amount(found.getAmount())
            .offererName("수정된 이름")
            .prayerContent("수정된 기도제목")
            .usedPoint(found.getUsedPoint())
            .build();
        repository.save(updated);
        repository.flush();

        assertThat(repository.findById(id).get().getOffererName()).isEqualTo("수정된 이름");
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