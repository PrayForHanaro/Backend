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
@Table(name = "Activity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Activity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long activityId;

	@Column(name = "orgId", nullable = false)
	private Long orgId;

	@Column(name = "creatorId", nullable = false)
	private Long creatorId;

	@Enumerated(EnumType.STRING)
	@Column(name = "activityCategory", nullable = false, length = 20)
	private ActivityCategory activityCategory;

	@Enumerated(EnumType.STRING)
	@Column(name = "activityType", nullable = false, length = 20)
	private ActivityType activityType;

	@Enumerated(EnumType.STRING)
	@Column(name = "activityState", nullable = false, length = 20)
	@Builder.Default
	private ActivityState activityState = ActivityState.RECRUITING;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "location", length = 255)
	private String location;

	@Column(name = "maxMembers", nullable = false)
	private int maxMembers;

	@Column(name = "startAt", nullable = false)
	private LocalDateTime startAt;

	@Column(name = "endAt", nullable = false)
	private LocalDateTime endAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "recurrence")
	private RecurrenceType recurrence;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
			name = "ActivityRecurrenceDay",
			joinColumns = @JoinColumn(name = "activityId")
	)
	@Enumerated(EnumType.STRING)
	@Column(name = "recurrenceDay")
	@Builder.Default
	private List<DayOfWeekType> recurrenceDays = new ArrayList<>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
			name = "ActivityRecurrenceMonthDay",
			joinColumns = @JoinColumn(name = "activityId")
	)
	@Column(name = "recurrenceMonthDay")
	@Builder.Default
	private List<Integer> recurrenceMonthDays = new ArrayList<>();

	@Column(name = "pointAmount", nullable = false)
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