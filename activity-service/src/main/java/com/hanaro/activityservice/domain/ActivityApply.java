package com.hanaro.activityservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
	name = "ACTIVITY_APPLY",
	uniqueConstraints = @UniqueConstraint(
		columnNames = {"activity_id", "user_id"}
	)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ActivityApply extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long applyId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id", nullable = false)
	private Activity activity;  // 같은 DB → FK 정상

	@Column(nullable = false)
	private Long userId;        // FK 없음 (user_db)

	@Column(nullable = false)
	private int pointEarned;

}