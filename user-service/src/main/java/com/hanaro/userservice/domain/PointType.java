package com.hanaro.userservice.domain;

/**
 * OFFERING_ONCE,       // 일시헌금 (헌금액 × donationRate, 가변)
 * OFFERING_RECURRING,  // 정기헌금 등록 (500p 고정)
 * ACTIVITY_VOLUNTEER,  // 봉사 활동 참여 (100p 고정)
 * ACTIVITY_CHURCH,     // 교회 행사 참여 (50p 고정)
 * SAVINGS_JOIN         // 정기 적금 가입 (5000p 고정)
 */
public enum PointType {
	OFFERING_ONCE, OFFERING_RECURRING, ACTIVITY_VOLUNTEER, ACTIVITY_CHURCH, SAVINGS_JOIN
}
