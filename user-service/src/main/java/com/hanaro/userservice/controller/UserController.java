package com.hanaro.userservice.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.userservice.dto.UserHomeResponse;
import com.hanaro.userservice.dto.UserGivingResponse;
import com.hanaro.userservice.dto.UserSimpleResponse;
import com.hanaro.userservice.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "유저", description = "유저 정보 핸들링")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me/home")
	public ApiResponse<UserHomeResponse> getHome(@AuthenticationPrincipal CustomUserDetails user) {
		return ApiResponse.ok(userService.getHomeInfo(user.getUserId()));
	}

	@GetMapping("/me/givingOnce")
	public ApiResponse<UserGivingResponse> getGiving(@AuthenticationPrincipal CustomUserDetails user) {
		return ApiResponse.ok(userService.getGivingInfo(user.getUserId()));
	}

	@GetMapping("/list")
	public ApiResponse<List<UserSimpleResponse>> getList(@RequestParam List<Long> ids) {
		return ApiResponse.ok(userService.getUserList(ids));
	}

	/** 포인트 사용 API */
	public record PointUseRequest(int amount, Long refId) {}

	@PostMapping("/me/points/use")
	public ApiResponse<Void> usePoints(
			@AuthenticationPrincipal CustomUserDetails user,
			@RequestBody PointUseRequest request) {
		userService.usePoints(user.getUserId(), request.amount(), request.refId());
		return ApiResponse.ok();
	}
}
