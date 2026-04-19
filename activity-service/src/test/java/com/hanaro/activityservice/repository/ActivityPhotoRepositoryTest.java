package com.hanaro.activityservice.repository;

import com.hanaro.activityservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class ActivityPhotoRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;

    @Autowired
    private ActivityPhotoRepository repository;
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
                .title("사진테스트활동")
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
        ActivityPhoto photo = ActivityPhoto.builder()
            .activity(savedActivity)
            .photoUrl("http://test.url/photo.jpg")
            .orderNum(1)
            .build();
        ActivityPhoto saved = repository.save(photo);
        id = saved.getPhotoId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        ActivityPhoto found = repository.findById(id).orElseThrow();
        assertThat(found.getPhotoUrl()).isEqualTo("http://test.url/photo.jpg");
    }

    @Test
    @Order(3)
    void updateTest() {
        ActivityPhoto found = repository.findById(id).orElseThrow();
        ActivityPhoto updated = ActivityPhoto.builder()
            .photoId(found.getPhotoId())
            .activity(found.getActivity())
            .photoUrl("http://test.url/updated.jpg")
            .orderNum(found.getOrderNum())
            .build();
        repository.save(updated);
        repository.flush();

        assertThat(repository.findById(id).get().getPhotoUrl())
            .isEqualTo("http://test.url/updated.jpg");
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