package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 일회성 송금 (축복 보내기)
 * - 상대방 계좌로 즉시 송금
 * - 기도말씀 함께 전송 (선택)
 * - 카카오톡 알림 발송
 */
@Entity
@Table(name = "TRANSFER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Transfer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transferId;

	/** 보내는 사람 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long senderId;

	/** 받는 사람 계좌번호 */
	@Column(nullable = false, length = 30)
	private String receiverAccountNumber;

	/** 송금 금액 */
	@Column(nullable = false)
	private BigDecimal amount;

	/** 기도말씀 (생략 가능) */
	@Column(length = 500)
	private String prayerMessage;

	/** 카카오톡 알림 발송 여부 */
	@Column(nullable = false)
	private boolean kakaoSent;

	/** 생성일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.kakaoSent = false;
	}

	/** 카카오 알림 발송 완료 처리 */
	public void markKakaoSent() {
		this.kakaoSent = true;
	}
}