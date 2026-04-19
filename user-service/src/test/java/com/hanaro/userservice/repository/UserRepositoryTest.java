package com.hanaro.userservice.repository;

import com.hanaro.userservice.domain.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class UserRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;
    private static final String testPhone = "010-" + ((System.nanoTime() + 2L) % 100000000L);

    @Autowired
    private UserRepository repository;

    private final User newUser = User.builder()
        .name("테스트유저")
        .birthDate(LocalDate.of(1990, 1, 1))
        .phone(testPhone)
        .password("password123")
        .build();

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
    }

    @Test
    @Order(1)
    void writeTest() {
        User saved = repository.save(newUser);
        assertThat(saved).usingRecursiveComparison()
            .ignoringFields("userId", "createdAt", "updatedAt",
                "donationRate", "role", "pointSum", "accounts", "points", "defaultAccount")
            .isEqualTo(newUser);
        id = saved.getUserId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        User found = repository.findById(id).orElseThrow();
        assertThat(found.getPhone()).isEqualTo(newUser.getPhone());
        System.out.println("found = " + found.getName());
    }

    @Test
    @Order(3)
    void updateTest() {
        User found = repository.findById(id).orElseThrow();
        User updated = User.builder()
            .userId(found.getUserId())
            .name("수정된유저")
            .birthDate(found.getBirthDate())
            .phone(found.getPhone())
            .password(found.getPassword())
            .donationRate(found.getDonationRate())
            .role(found.getRole())
            .build();
        repository.save(updated);
        repository.flush();

        assertThat(repository.findById(id).get().getName()).isEqualTo("수정된유저");
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