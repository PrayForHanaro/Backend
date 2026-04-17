package com.hanaro.userservice.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/points")
public class PointController {

  //포인트 생성

  @PostMapping("/points")
  void createPoint(PointRequest request);
}
