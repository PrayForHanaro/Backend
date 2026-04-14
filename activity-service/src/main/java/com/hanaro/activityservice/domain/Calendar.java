package com.hanaro.activityservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CALENDAR")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long calendarId;

	@Column(nullable = false)
	private Long userId;        // FK 없음 (user_db)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id")
	private Activity activity;  // 같은 DB → FK 정상 (nullable)

	@Column(nullable = false, length = 10)
	private String calendarType;    // 개인/모임/교회

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false)
	private LocalDateTime startDate;

	private LocalDateTime endDate;

	@Column(length = 500)
	private String memo;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}