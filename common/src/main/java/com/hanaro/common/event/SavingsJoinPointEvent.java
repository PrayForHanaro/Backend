package com.hanaro.common.event;

import com.hanaro.common.domain.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsJoinPointEvent {
  private Long userId;
  private PointType pointType;
  String productName;
  String targetName;
}
