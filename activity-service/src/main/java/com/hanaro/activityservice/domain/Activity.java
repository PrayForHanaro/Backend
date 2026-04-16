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
public class Activity extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long activityId;

	@Column(nullable = false)
	private Long orgId;         // FK 없음 (org_db)

	@Column(nullable = false)
	private Long creatorId;     // FK 없음 (user_db)

	@Enumerated(EnumType.STRING)
	private ActivityCategory activityCategory;  // 봉사모집/동행찾기/교회행사

	@Column(nullable = false, length = 10)
	private ActivityType activityType;      // 일회성/정기

	@Column(nullable = false, length = 15)
	@Enumerated(EnumType.STRING)
	private ActivityState activityState;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(columnDefinition = "TEXT", length = 1000)
	private String description;

	@Column(length = 255)
	private String location;

	@Column(nullable = false)
	private int maxMembers;

	@Column(nullable = false)
	private LocalDateTime startAt; //정기, 비정기 모두 시간 설정 필수

	@Column(nullable = false)
	private LocalDateTime endAt; //정기, 비정기 모두 시간 설정 필수

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private RecurrenceType recurrence;      // 정기일 경우에만 선택. 매일/매주/매월

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	private List<DayOfWeekType> recurrenceDays = new ArrayList<>(); //정기-매주 인 경우 설정

	@Column(nullable = true)
	private Integer recurrenceDayOfMonth; // 정기 - 매주인 경우

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
}
