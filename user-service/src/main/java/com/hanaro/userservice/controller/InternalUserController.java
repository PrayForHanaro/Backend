package com.hanaro.userservice.controller;

import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.userservice.dto.request.UsePointRequest;
import com.hanaro.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

  private final UserService userService;

  @PostMapping("/{userId}/points/use")
  public ResponseEntity<Void> usePoint(
      @PathVariable Long userId,
      @RequestBody UsePointRequest request
  ) {
    userService.usePoint(userId, request);
    return ResponseEntity.ok().build();
  }
}
