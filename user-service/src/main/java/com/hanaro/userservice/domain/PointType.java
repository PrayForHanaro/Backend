package com.hanaro.userservice.domain;

// * OFFERING_RECURRING  500p  정기헌금 등록
// * ACTIVITY_VOLUNTEER  100p  봉사 활동 참여
// * ACTIVITY_CHURCH      50p  교회 행사 참여
// * SAVINGS_JOIN       5000p  정기 적금 가입
// 포인트 지급액은 변경가능
public enum PointType {

	OFFERING_ONCE,
	OFFERING_RECURRING,
	ACTIVITY_VOLUNTEER,
	ACTIVITY_CHURCH,
	SAVINGS_JOIN,
	SAVINGS_RECURRING
}
