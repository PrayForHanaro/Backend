package com.hanaro.activityservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
	name = "ActivityApply",
	uniqueConstraints = @UniqueConstraint(
		columnNames = {"activityId", "userId"}
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
	@JoinColumn(name = "activityId", nullable = false)
	private Activity activity;

	@Column(name = "userId", nullable = false)
	private Long userId;

	@Column(name = "pointEarned", nullable = false)
	private int pointEarned;

}