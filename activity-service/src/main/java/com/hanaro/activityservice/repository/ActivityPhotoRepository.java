package com.hanaro.activityservice.repository;

import com.hanaro.activityservice.domain.ActivityPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityPhotoRepository extends JpaRepository<ActivityPhoto, Long> {
}