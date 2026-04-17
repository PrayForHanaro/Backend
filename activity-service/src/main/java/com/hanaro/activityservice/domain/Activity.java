package com.hanaro.activityservice.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ACTIVITY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Activity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long activityId;

	@Column(nullable = false)
	private Long orgId;         // FK 없음 (org_db)

	@Column(nullable = false)
	private Long creatorId;     // FK 없음 (user_db)

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ActivityCategory activityCategory;  // 봉사모집/동행찾기/교회행사

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ActivityType activityType;      // 일회성/정기

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private ActivityState activityState = ActivityState.RECRUITING;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 255)
	private String location;

	@Column(nullable = false)
	private int maxMembers;

	@Column(nullable = false)
	private LocalDateTime startAt; // 정기, 비정기 모두 시간 설정 필수

	@Column(nullable = false)
	private LocalDateTime endAt; // 정기, 비정기 모두 시간 설정 필수

	@Enumerated(EnumType.STRING)
	private RecurrenceType recurrence;      // 정기일 경우에만 선택. 매일/매주/매월

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
			name = "ACTIVITY_RECURRENCE_DAY",
			joinColumns = @JoinColumn(name = "activity_id")
	)
	@Enumerated(EnumType.STRING)
	@Column(name = "recurrence_day")
	@Builder.Default
	private List<DayOfWeekType> recurrenceDays = new ArrayList<>(); // 정기-매주인 경우 설정

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
			name = "ACTIVITY_RECURRENCE_MONTH_DAY",
			joinColumns = @JoinColumn(name = "activity_id")
	)
	@Column(name = "recurrence_month_day")
	@Builder.Default
	private List<Integer> recurrenceMonthDays = new ArrayList<>(); // 정기-매월인 경우 설정

	@Column(nullable = false)
	private int pointAmount;

	@OneToMany(mappedBy = "activity", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ActivityPhoto> photos = new ArrayList<>();

	@OneToMany(mappedBy = "activity", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ActivityApply> applies = new ArrayList<>();

	public void addPhoto(String photoUrl, int orderNum) {
		ActivityPhoto photo = ActivityPhoto.builder()
				.activity(this)
				.photoUrl(photoUrl)
				.orderNum(orderNum)
				.build();

		photos.add(photo);
	}

	public void close() {
		this.activityState = ActivityState.CLOSED;
	}
}