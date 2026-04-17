package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.PrayerSavings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrayerSavingsRepository extends JpaRepository<PrayerSavings, Long> {

    /** Gift에 속한 메시지 최신순 (BLESS_SPEC §2-11 타임라인) */
    @Query("SELECT ps FROM PrayerSavings ps WHERE ps.gift.giftId = :giftId ORDER BY ps.createdAt DESC")
    Page<PrayerSavings> findByGiftId(@Param("giftId") Long giftId, Pageable pageable);

    /** 메시지 수정·삭제 권한 체크. 소유자(Gift.senderId) 일치 여부 */
    @Query("SELECT ps FROM PrayerSavings ps WHERE ps.prayerSavingsId = :id AND ps.gift.senderId = :senderId")
    Optional<PrayerSavings> findByIdAndSenderId(@Param("id") Long id, @Param("senderId") Long senderId);
}
