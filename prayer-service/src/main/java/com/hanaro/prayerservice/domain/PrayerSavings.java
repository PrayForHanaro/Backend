package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 기도 메시지 (Gift 1 : N PrayerSavings)
 * - 송금과 독립적으로 작성/수정/삭제 가능 (BLESS_SPEC §3)
 * - daysOfPrayer는 startDate 기반 실시간 계산 (BLESS_SPEC §2-6c) — dDay 컬럼 저장 안 함
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PrayerSavings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class PrayerSavings extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prayerSavingsId")
	private Long prayerSavingsId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "giftId",
		nullable = false,
		foreignKey = @ForeignKey(name = "fk_prayer_savings_gift"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Gift gift;

	/** 기도문 (BLESS_SPEC §3-5 최대 250자) */
	@Column(name = "prayerContent", nullable = false, length = 250)
	private String prayerContent;

	/** 기도 시작일 (작성 시점) */
	@Column(name = "startDate", nullable = false)
	private LocalDate startDate;

	public void updateContent(String newContent) {
		this.prayerContent = newContent;
	}
}
