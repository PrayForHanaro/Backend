package com.hanaro.orgservice.dto;


import com.hanaro.orgservice.domain.ReligiousOrg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrgMyPageResponseDTO {
  String name;
  public static OrgMyPageResponseDTO from(ReligiousOrg org) {
    return OrgMyPageResponseDTO.builder()
        .name(org.getOrgName())
        .build();
  }
}
