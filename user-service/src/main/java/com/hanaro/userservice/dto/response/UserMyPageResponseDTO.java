package com.hanaro.userservice.dto.response;

import com.hanaro.userservice.domain.Point;
import com.hanaro.userservice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMyPageResponseDTO {
  private String name;
  private String profileUrl;
  private String orgName;
  private int pointSum;

  public static UserMyPageResponseDTO of(User user, OrgMyPageResponseDTO orgDto) {
    return UserMyPageResponseDTO.builder()
        .name(user.getName())
        .profileUrl(user.getProfileUrl())
        .orgName(orgDto.getName())
        .pointSum(user.getPointSum())
        .build();
  }
}
