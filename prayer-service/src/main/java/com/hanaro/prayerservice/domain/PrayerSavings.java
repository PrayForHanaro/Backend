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
@Table(name = "PrayerSavings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class PrayerSavings extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prayerSavingsId", columnDefinition = "int unsigned")
	private Long prayerSavingsId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "giftId", referencedColumnName = "giftId",
		columnDefinition = "int unsigned",
		foreignKey = @ForeignKey(name = "fk_Prayer_Savings_Gift"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Gift gift;

	@Column(name = "prayerContent", nullable = false, length = 100)
	private String prayerContent;

	@Column(name = "startDate", nullable = false)
	private LocalDate startDate;

	@Column(name = "dDay", nullable = false)
	private int dDay;


	@PrePersist
	protected void onCreate() {
		this.dDay = 0;
	}
}
