package com.hanaro.userservice.dto;

import java.math.BigDecimal;

public record UserGivingResponse(
    String name, 
    BigDecimal maxPoint, 
    String bankAccount, 
    Long orgId, 
    Long accountId // 헌금 시 필요한 계좌 ID 추가
) {}
