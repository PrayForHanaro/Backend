package com.hanaro.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrgResponse {
    private Long orgId;
    private String orgName;
    private String orgType;
    private String address;
}
