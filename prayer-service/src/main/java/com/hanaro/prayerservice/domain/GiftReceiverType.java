package com.hanaro.prayerservice.domain;

/**
 * 대상자와의 관계.
 * 표시 문구(한글)는 presentation 레이어(FE)에서 매핑.
 * 성별 구분(아들/딸/손자/손녀 등) 필요 시 별도 enum 확장 또는 gender 필드 추가 (BLESS_SPEC §1-6 추후 결정).
 */
public enum GiftReceiverType {
    CHILD,
    GRANDCHILD,
    GREAT_GRANDCHILD
}
