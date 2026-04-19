package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class SavingsProductRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;

    @Autowired
    private SavingsProductRepository repository;

    private final SavingsProduct newProduct = SavingsProduct.builder()
        .name("희망적금")
        .interestRate(new BigDecimal("4.500"))
        .isActive(true)
        .build();

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
    }

    @Test
    @Order(1)
    void writeTest() {
        SavingsProduct saved = repository.save(newProduct);
        assertThat(saved).usingRecursiveComparison()
            .ignoringFields("savingsProductId", "createdAt", "updatedAt")
            .isEqualTo(newProduct);
        id = saved.getSavingsProductId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        SavingsProduct found = repository.findById(id).orElseThrow();
        assertThat(found.getName()).isEqualTo(newProduct.getName());
        System.out.println("found = " + found.getName());
    }

    @Test
    @Order(3)
    void updateTest() {
        SavingsProduct found = repository.findById(id).orElseThrow();
        found.updateName("수정된 적금");
        repository.save(found);
        repository.flush();

        assertThat(repository.findById(id).get().getName()).isEqualTo("수정된 적금");
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