package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findAllBySenderIdAndIsActiveTrue(Long senderId);
}
