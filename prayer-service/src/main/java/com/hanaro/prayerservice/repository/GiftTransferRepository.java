package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.GiftTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftTransferRepository extends JpaRepository<GiftTransfer, Long> {

    /** Gift별 이체 이력 최신순 (BLESS_SPEC §2-3/2-4 타임라인) */
    @Query("SELECT gt FROM GiftTransfer gt WHERE gt.gift.giftId = :giftId ORDER BY gt.transferDate DESC")
    Page<GiftTransfer> findByGiftId(@Param("giftId") Long giftId, Pageable pageable);
}
