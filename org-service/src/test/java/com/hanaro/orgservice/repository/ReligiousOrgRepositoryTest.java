package com.hanaro.orgservice.repository;

import com.hanaro.orgservice.domain.OrgType;
import com.hanaro.orgservice.domain.ReligiousOrg;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class ReligiousOrgRepositoryTest extends BaseRepositoryTest {
    private static long id;
    private static long orgCount = 0;

    private final ReligiousOrg newOrg = ReligiousOrg.builder()
        .orgType(OrgType.교회)
        .orgName("하나교회")
        .address("서울시 중구 을지로")
        .phone("02-123-4567")
        .representativeId(1L)
        .accountId(100L)
        .totalOfferingAmount(BigDecimal.ZERO)
        .totalPointAmount(BigDecimal.ZERO)
        .build();

    @Autowired
    private ReligiousOrgRepository repository;

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
    }

    @Test
    @Order(1)
    void writeTest() {
        System.out.println("Executing writeTest...");
        ReligiousOrg saved = repository.save(newOrg);
        assertThat(saved).usingRecursiveComparison()
            .ignoringFields("religiousOrgId", "createdAt", "updatedAt", "totalOfferingAmount", "totalPointAmount")
            .isEqualTo(newOrg);
        id = saved.getReligiousOrgId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        System.out.println("Executing readTest...");
        ReligiousOrg found = repository.findById(id).orElseThrow();
        assertThat(found.getOrgName()).isEqualTo(newOrg.getOrgName());
    }

    @Test
    @Order(3)
    void updateTest() {
        System.out.println("Executing updateTest...");
        ReligiousOrg found = repository.findById(id).orElseThrow();

        ReligiousOrg updatedOrg = ReligiousOrg.builder()
            .religiousOrgId(found.getReligiousOrgId())
            .orgType(found.getOrgType())
            .orgName("수정된하나교회")
            .address("서울시 강남구")
            .phone(found.getPhone())
            .representativeId(found.getRepresentativeId())
            .accountId(found.getAccountId())
            .totalOfferingAmount(found.getTotalOfferingAmount())
            .totalPointAmount(found.getTotalPointAmount())
            .build();

        repository.save(updatedOrg);
        repository.flush();

        ReligiousOrg foundUpdated = repository.findById(id).orElseThrow();
        assertThat(foundUpdated.getOrgName()).isEqualTo("수정된하나교회");
    }

    @Test
    @Order(4)
    void deleteTest() {
        System.out.println("Executing deleteTest...");
        repository.deleteById(id);
        repository.flush();
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @Order(5)
    void finalCheck() {
        System.out.println("Executing finalCheck...");
        assertThat(repository.count()).isEqualTo(orgCount);
    }
}