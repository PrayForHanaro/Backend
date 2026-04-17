package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {

    List<Gift> findAllBySenderIdAndIsActiveTrue(Long senderId);

    Optional<Gift> findByGiftIdAndSenderId(Long giftId, Long senderId);

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
