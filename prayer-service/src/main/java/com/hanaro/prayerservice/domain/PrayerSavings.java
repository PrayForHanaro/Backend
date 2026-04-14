package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

//  gift:prayerSavings =  1:다 / 여기는 기도문 만
/**
 * 기도문 리스트
 * 송금이랑 아예 별개로 관리됨
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
	@Column(columnDefinition = "int unsigned")
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


	@PrePersist
	protected void onCreate() {
		this.dDay = 0;
	}
}
