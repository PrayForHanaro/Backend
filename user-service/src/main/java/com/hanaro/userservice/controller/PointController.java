package com.hanaro.userservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.userservice.dto.response.PageResponseDTO;
import com.hanaro.userservice.dto.response.PointResponseDTO;
import com.hanaro.userservice.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/user/points")
@RequiredArgsConstructor
public class PointController {

  private final PointService pointService;

  @GetMapping
  public ApiResponse<PageResponseDTO<PointResponseDTO>> getPointList(
      @RequestHeader("X-Auth-User-Id") Long userId,
      @PageableDefault(size = 10,
          sort = "createdAt",
          direction = Sort.Direction.DESC) Pageable pageable
  ) {
    return ApiResponse.ok(pointService.getPointList(userId, pageable));
  }
}