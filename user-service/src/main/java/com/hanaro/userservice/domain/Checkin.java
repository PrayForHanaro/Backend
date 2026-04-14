package com.hanaro.userservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 앱 진입 체크인 이력
 * - 앱 최초 화면에서 가운데 버튼 클릭 시 기록
 * - 미클릭 시 미체크인 기간 증가
 * - 일정 기간 미접속 시 구역원들에게 안부묻기 버튼 노출
 */

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CHECKIN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Checkin extends BaseEntity {

	// 살아있슈를 빼서 체크인 자체를 빼도 될 듯 ?
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long checkinId;

	/** 체크인한 성도 (같은 DB → FK 정상 사용) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** 체크인 일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime checkedAt;

	@PrePersist
	protected void onCreate() {
		this.checkedAt = LocalDateTime.now();
	}
}