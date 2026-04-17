package com.hanaro.userservice.dto.response;

import com.hanaro.userservice.domain.Point;
import com.hanaro.userservice.domain.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResponseDTO {
  private Long pointId;
  private PointType pointType;
  private int amount;
  private String info;

  public static PointResponseDTO from(Point point) {
    return PointResponseDTO.builder()
        .pointId(point.getPointId())
        .pointType(point.getPointType())
        .amount(point.getAmount())
        .info(point.getInfo())
        .build();
  }
}
