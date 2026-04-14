package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 증여 이체 로그
 * - 실제 이체된 내역 기록
 * - 메시지 보내고 싶을 때만 작성 가능
 * - 누적 송금액 표시
 */
@Entity
@Table(name = "GIFT_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class GiftLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long logId;

	/** 증여 설정 ID (같은 DB → FK 정상 사용) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gift_id", nullable = false)
	private Gift gift;

	/** 보내는 사람 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long senderId;

	/** 받는 사람 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long receiverId;

	/** 이체 금액 (0이면 메시지만 전송) */
	@Column(nullable = false)
	private BigDecimal amount;

	/** 메시지 (보내고 싶을 때만) */
	@Column(length = 500)
	private String message;

	/** 이 시점까지의 누적 송금 총액 */
	@Column(nullable = false)
	private BigDecimal cumulativeTotal;

	/** 이체일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime transferredAt;

	@PrePersist
	protected void onCreate() {
		this.transferredAt = LocalDateTime.now();
	}
}