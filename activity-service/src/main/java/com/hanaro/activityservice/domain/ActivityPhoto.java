package com.hanaro.activityservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ActivityPhoto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ActivityPhoto extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long photoId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activityId", nullable = false)
	private Activity activity;

	@Column(name = "photoUrl", nullable = false, length = 500)
	private String photoUrl;

	@Column(name = "orderNum", nullable = false)
	private int orderNum;
}