package com.hanaro.userservice.repository;

import com.hanaro.userservice.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByUser_UserId(Long userId);
}
