package com.hanaro.userservice.repository;

import com.hanaro.userservice.domain.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    Page<Point> findByUser_UserId(Long userUserId, Pageable pageable);
}

