package com.hanaro.orgservice.dto;

import java.math.BigDecimal;

public record OrgSummaryResponse(String orgName, BigDecimal totalDonation) {}
