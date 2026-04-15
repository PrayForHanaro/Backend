package com.hanaro.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.userservice.dto.UserGivingOnceDTO;
import com.hanaro.userservice.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "유저", description = "유저 정보 핸들링")
@RestController
@RequestMapping("api/user")
public class UserController {

	private UserService userService;

	@GetMapping("/me")
	public ResponseEntity<UserGivingOnceDTO> findGivingOnceUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
		if (userDetails == null) {
			return ResponseEntity.status(401).build();
		}
		Long memberId = userDetails.getUserId();
		return ResponseEntity.ok(userService.getGivingOnceUserInfo(memberId));
	}
}
