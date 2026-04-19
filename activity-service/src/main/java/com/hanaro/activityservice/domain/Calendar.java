package com.hanaro.activityservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Calendar")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Calendar extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long calendarId;

	@Column(name = "userId", nullable = false)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activityId")
	private Activity activity;

	@Enumerated(EnumType.STRING)
	@Column(name = "calendarType", nullable = false, length = 10)
	private CalendarType calendarType;

	@Column(name = "title", nullable = false, length = 20)
	private String title;

	@Column(name = "startDateTime", nullable = false)
	private LocalDateTime startDateTime;

	@Column(name = "endDateTime", nullable = false)
	private LocalDateTime endDateTime;

	@Column(name = "memo", length = 100)
	private String memo;

}