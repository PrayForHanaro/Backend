package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {

    List<Gift> findAllBySenderIdAndIsActiveTrue(Long senderId);

    Optional<Gift> findByGiftIdAndSenderId(Long giftId, Long senderId);

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    /**
     * 오늘 자동이체 실행 대상 Gift 조회 (BLESS_SPEC §6).
     * 말일 보정: 오늘이 해당 월의 말일이면 transferDay > 오늘(예: 31)도 함께 포함.
     */
    @Query("SELECT g FROM Gift g WHERE g.isActive = true AND " +
           "(g.transferDay = :today OR (:isLastDayOfMonth = true AND g.transferDay > :today))")
    List<Gift> findDueGifts(@Param("today") int today,
                            @Param("isLastDayOfMonth") boolean isLastDayOfMonth);
}
