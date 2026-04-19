package com.hanaro.activityservice.repository;

import com.hanaro.activityservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class ActivityApplyRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;

    @Autowired
    private ActivityApplyRepository repository;
    @Autowired
    private ActivityRepository activityRepository;

    private static Activity savedActivity;

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
        if (savedActivity == null) {
            savedActivity = activityRepository.save(Activity.builder()
                .orgId(1L)
                .creatorId(1L)
                .activityCategory(ActivityCategory.봉사모집)
                .activityType(ActivityType.ONE_TIME)
                .activityState(ActivityState.RECRUITING)
                .title("신청테스트활동")
                .description("내용")
                .location("위치")
                .maxMembers(10)
                .startAt(LocalDateTime.of(2026, 5, 1, 9, 0))
                .endAt(LocalDateTime.of(2026, 5, 1, 18, 0))
                .pointAmount(100)
                .build());
        }
    }

    @Test
    @Order(1)
    void writeTest() {
        ActivityApply apply = ActivityApply.builder()
            .activity(savedActivity)
            .userId(1L)
            .pointEarned(100)
            .build();
        ActivityApply saved = repository.save(apply);
        id = saved.getApplyId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        ActivityApply found = repository.findById(id).orElseThrow();
        assertThat(found.getUserId()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    void updateTest() {
        ActivityApply found = repository.findById(id).orElseThrow();
        ActivityApply updated = ActivityApply.builder()
            .applyId(found.getApplyId())
            .activity(found.getActivity())
            .userId(found.getUserId())
            .pointEarned(200)
            .build();
        repository.save(updated);
        repository.flush();

        assertThat(repository.findById(id).get().getPointEarned()).isEqualTo(200);
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