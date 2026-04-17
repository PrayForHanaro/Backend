package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.OnceTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnceTransferRepository extends JpaRepository<OnceTransfer, Long> {

    /** 사용자별 일회성 송금 이력 최신순 (BLESS_SPEC §5) */
    Page<OnceTransfer> findBySenderIdOrderBySentAtDesc(Long senderId, Pageable pageable);
}
