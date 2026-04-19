package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class OnceTransferRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;

    @Autowired
    private OnceTransferRepository repository;

    private final OnceTransfer newTransfer = OnceTransfer.builder()
            .senderId(1L)
            .fromAccountId(100L)
            .toAccountNumber("111-222-333")
            .amount(new BigDecimal("5000.00"))
            .message("축복합니다")
            .sentAt(Instant.now())
            .status(TransferStatus.PENDING)
            .build();

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
    }

    @Test
    @Order(1)
    void writeTest() {
        OnceTransfer saved = repository.save(newTransfer);
        assertThat(saved).usingRecursiveComparison()
                .ignoringFields("onceTransferId", "createdAt", "updatedAt")
                .isEqualTo(newTransfer);
        id = saved.getOnceTransferId();
    }

    @Test
    @Order(2)
    void readTest() {
        OnceTransfer found = repository.findById(id).orElseThrow();
        assertThat(found.getToAccountNumber()).isEqualTo(newTransfer.getToAccountNumber());
    }

    @Test
    @Order(3)
    void updateTest() {
        OnceTransfer found = repository.findById(id).orElseThrow();
        found.markSuccess(Instant.now());
        repository.save(found);
        repository.flush();
        
        assertThat(repository.findById(id).get().getStatus()).isEqualTo(TransferStatus.SUCCESS);
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
    }
}
