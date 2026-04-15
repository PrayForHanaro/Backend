package com.hanaro.orgservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrgResponseDTO {
    private Long orgId;
    private String orgName;
    private String orgType;
    private String address;
}
