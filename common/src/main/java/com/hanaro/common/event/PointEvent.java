package com.hanaro.common.event;

import com.hanaro.common.domain.PointType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointEvent {
  private Long userId;
  private PointType pointType;

  private Map<String, String> context;
}
