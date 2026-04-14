package com.hanaro.activityservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ACTIVITY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long activityId;

	@Column(nullable = false)
	private Long orgId;         // FK 없음 (org_db)

	@Column(nullable = false)
	private Long creatorId;     // FK 없음 (user_db)

	@Column(nullable = false, length = 20)
	private String activityCategory;  // 봉사모집/동행찾기/교회행사

	@Column(nullable = false, length = 10)
	private String activityType;      // 일회성/정기

	@Column(nullable = false, length = 100)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 255)
	private String location;

	@Column(nullable = false)
	private int maxMembers;

	@Column(nullable = false)
	private LocalDateTime startAt;

	private LocalDateTime endAt;

	@Column(length = 10)
	private String recurrence;      // 매일/매주/매월

	@Column(length = 20)
	private String recurrenceDay;   // 요일 선택 시

	@Column(nullable = false)
	private int pointAmount;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ActivityPhoto> photos = new ArrayList<>();

	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ActivityApply> applies = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}