package com.hanaro.activityservice.repository;

import com.hanaro.activityservice.domain.ActivityApply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityApplyRepository extends JpaRepository<ActivityApply, Long> {
    long countByActivity_ActivityId(Long activityId);
    boolean existsByActivity_ActivityIdAndUserId(Long activityId, Long userId);
}