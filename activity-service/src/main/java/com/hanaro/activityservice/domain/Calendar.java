package com.hanaro.activityservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CALENDAR")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Calendar extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long calendarId;

	@Column(nullable = false)
	private Long userId;        // FK 없음 (user_db)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id")
	private Activity activity;  // 같은 DB → FK 정상 (nullable)

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private CalendarType calendarType;    // 개인/교회

	@Column(nullable = false, length = 20)
	private String title;

	@Column(nullable = false)
	private LocalDateTime startDateTime;

	@Column(nullable = false)
	private LocalDateTime endDateTime;

	@Column(length = 100)
	private String memo;

}