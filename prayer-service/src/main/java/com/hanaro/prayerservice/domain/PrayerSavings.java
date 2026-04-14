package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//  gift:prayerSavings =  1:다 / 여기는 기도문 만
/**
 * 기도 적금
 * - 기도 제목을 걸고 적금 납입
 * - D+N / 총 N일 형태로 진행 현황 표시
 * - 목표 금액 달성 여부 추적
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PRAYER_SAVINGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class PrayerSavings extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long prayerSavingsId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gift", referencedColumnName = "id",
		columnDefinition = "int unsigned",
		foreignKey = @ForeignKey(name = "fk_Prayer_Savings_Gift"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Gift gift;

	/** 기도문  */
	@Column(nullable = false, length = 100)
	private String prayerContent;

	/** 시작일 */
	@Column(nullable = false)
	private LocalDate startDate;

	/** D+N 현재 경과일수 */
	@Column(nullable = false)
	private int dDay;

	/** 총 기도 목표일수 (기본 100일) */
	@Column(nullable = false)
	private int totalDays;

	/** 활성화 여부 */
	@Column(nullable = false)
	private boolean isActive;

	/** 생성일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.isActive = true;
		this.currentAmount = BigDecimal.ZERO;
		this.dDay = 0;
	}
}