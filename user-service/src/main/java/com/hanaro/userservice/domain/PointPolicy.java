package com.hanaro.userservice.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 포인트 지급 정책 (고정값 관리)
 * - 관리자가 포인트 정책 변경 시 여기서 수정
 * - 일시헌금은 가변값(헌금액 × 기여율)이라 정책 없음
 *
 * 기본 데이터 (init.sql에서 INSERT)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * OFFERING_RECURRING  500p  정기헌금 등록
 * ACTIVITY_VOLUNTEER  100p  봉사 활동 참여
 * ACTIVITY_CHURCH      50p  교회 행사 참여
 * SAVINGS_JOIN       5000p  정기 적금 가입
 * SAVINGS_AUTO         10p  자동 이체 실행
 */
@Entity
@Table(name = "POINT_POLICY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class PointPolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long policyId;

	/** 포인트 지급 유형 (unique) */
	@Column(nullable = false, unique = true, length = 30)
	private String pointType;

	/** 고정 지급 포인트 */
	@Column(nullable = false)
	private int amount;

	/** 정책 설명 */
	@Column(length = 100)
	private String description;

	/** 포인트 정책 변경 */
	public void updateAmount(int amount) {
		this.amount = amount;
	}
}