package com.hanaro.orgservice.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 구역 / 교구
 * - 교회 내 소모임 단위
 * - 1구역, 2구역 ... N구역
 * - 메인화면에서 같은 구역 성도 리스트 표시
 * - 구역원 미접속 시 안부묻기 기능
 * - 같은 교회 내 구역 순서 중복 방지 (UniqueConstraint)
 */
@Entity
@Table(
	name = "DISTRICT",
	uniqueConstraints = @UniqueConstraint(
		columnNames = {"org_id", "district_order"}
	)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class District {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long districtId;

	/**
	 * 소속 교회/성당/절
	 * 같은 DB → FK 정상 사용
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id", nullable = false)
	private ReligiousOrg org;

	/**
	 * 구역 이름
	 * 예: 1구역, 2구역, 청년부, 장년부
	 */
	@Column(nullable = false, length = 50)
	private String districtName;

	/**
	 * 구역 정렬 순서
	 * 화면에 표시될 순서 (오름차순)
	 * 같은 교회 내 중복 불가
	 */
	@Column(nullable = false)
	private int districtOrder;

	/** 구역명 수정 */
	public void updateDistrictName(String districtName) {
		this.districtName = districtName;
	}

	/** 구역 순서 수정 */
	public void updateDistrictOrder(int districtOrder) {
		this.districtOrder = districtOrder;
	}
}