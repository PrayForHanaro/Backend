package com.hanaro.userservice.repository;

import com.hanaro.userservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class PointRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;
    private static final String testPhone = "010-" + ((System.nanoTime() + 1L) % 100000000L);

    @Autowired
    private PointRepository repository;
    @Autowired
    private UserRepository userRepository;

    private static User savedUser;

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
        if (savedUser == null) {
            savedUser = userRepository.save(User.builder()
                .name("성도")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phone(testPhone)
                .password("pwd")
                .build());
        }
    }

    @Test
    @Order(1)
    void writeTest() {
        Point newPoint = Point.createActivityVolunteer(savedUser, "환경미화");
        Point saved = repository.save(newPoint);
        id = saved.getPointId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        Point found = repository.findById(id).orElseThrow();
        assertThat(found.getAmount()).isEqualTo(100);
        System.out.println("found amount = " + found.getAmount());
    }

    @Test
    @Order(3)
    void updateTest() {
        Point found = repository.findById(id).orElseThrow();
        Point updated = Point.builder()
            .pointId(found.getPointId())
            .user(found.getUser())
            .pointType(found.getPointType())
            .amount(200)
            .info(found.getInfo())
            .build();
        repository.save(updated);
        repository.flush();

        assertThat(repository.findById(id).get().getAmount()).isEqualTo(200);
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