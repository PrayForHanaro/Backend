package com.hanaro.activityservice.repository;

import com.hanaro.activityservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class ActivityRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;

    @Autowired
    private ActivityRepository repository;

    private final Activity newActivity = Activity.builder()
        .orgId(1L)
        .creatorId(1L)
        .activityCategory(ActivityCategory.교회행사)        // ✅ 실제 enum 값으로 교체
        .activityType(ActivityType.ONE_TIME)
        .activityState(ActivityState.RECRUITING)
        .title("행복한 봉사활동")
        .description("봉사활동 내용")
        .location("서울시")
        .maxMembers(10)                                  // ✅ maxParticipants → maxMembers
        .startAt(LocalDateTime.of(2026, 5, 1, 9, 0))   // ✅ 추가
        .endAt(LocalDateTime.of(2026, 5, 1, 18, 0))    // ✅ 추가
        .pointAmount(100)                                // ✅ 추가
        .build();

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
    }

    @Test
    @Order(1)
    void writeTest() {
        Activity saved = repository.save(newActivity);
        assertThat(saved).usingRecursiveComparison()
            .ignoringFields("activityId", "createdAt", "updatedAt",
                "recurrenceDays", "recurrenceMonthDays", "photos", "applies")
            .isEqualTo(newActivity);
        id = saved.getActivityId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        Activity found = repository.findById(id).orElseThrow();
        assertThat(found.getTitle()).isEqualTo(newActivity.getTitle());
        System.out.println("found = " + found.getTitle());
    }

    @Test
    @Order(3)
    void updateTest() {
        Activity found = repository.findById(id).orElseThrow();
        Activity updated = Activity.builder()
            .activityId(found.getActivityId())
            .orgId(found.getOrgId())
            .creatorId(found.getCreatorId())
            .activityCategory(found.getActivityCategory())
            .activityType(found.getActivityType())
            .activityState(ActivityState.CLOSED)         // ✅ 상태 변경
            .title("수정된 봉사활동")                     // ✅ 제목 변경
            .description(found.getDescription())
            .location(found.getLocation())
            .maxMembers(found.getMaxMembers())
            .startAt(found.getStartAt())
            .endAt(found.getEndAt())
            .pointAmount(found.getPointAmount())
            .build();
        repository.save(updated);
        repository.flush();

        Activity foundUpdated = repository.findById(id).orElseThrow();
        assertThat(foundUpdated.getTitle()).isEqualTo("수정된 봉사활동");
        assertThat(foundUpdated.getActivityState()).isEqualTo(ActivityState.CLOSED);
    }

    @Test
    @Order(4)
    void deleteTest() {
        repository.deleteById(id);
        repository.flush();
        assertThat(repository.findById(id)).isEmpty();
        System.out.println("deleted id = " + id);
    }

    @Test
    @Order(5)
    void finalCheck() {
        assertThat(repository.count()).isEqualTo(orgCount);
        System.out.println("finalCheck count = " + repository.count());
    }
}