package com.hanaro.activityservice.repository;

import com.hanaro.activityservice.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findAllByOrgIdOrderByCreatedAtDesc(Long orgId);
    Optional<Activity> findByActivityId(Long activityId);
}