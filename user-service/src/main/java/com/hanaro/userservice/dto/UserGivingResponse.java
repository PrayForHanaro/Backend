package com.hanaro.userservice.dto;

import java.math.BigDecimal;

public record UserGivingResponse(
    String name, 
    BigDecimal maxPoint, 
    String bankAccount, 
    Long orgId, 
    Long accountId,
    Double donationRate
) {}
