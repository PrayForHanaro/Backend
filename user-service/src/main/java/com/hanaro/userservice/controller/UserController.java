package com.hanaro.userservice.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.userservice.dto.UserGivingResponseDTO;
import com.hanaro.userservice.dto.UserHomeResponseDTO;
import com.hanaro.userservice.dto.UserSimpleResponseDTO;
// import com.hanaro.userservice.domain.PointType;
import com.hanaro.userservice.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "유저", description = "유저 정보 핸들링")
@RestController
@RequestMapping("/apis/user/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me/home")
	public ApiResponse<UserHomeResponseDTO> getHome(@AuthenticationPrincipal CustomUserDetails user) {
		return ApiResponse.ok(userService.getHomeInfo(user.getUserId()));
	}

	@GetMapping("/me/givingOnce")
	public ApiResponse<UserGivingResponseDTO> getGiving(@AuthenticationPrincipal CustomUserDetails user) {
		return ApiResponse.ok(userService.getGivingInfo(user.getUserId()));
	}

	@GetMapping("/list")
	public ApiResponse<List<UserSimpleResponseDTO>> getList(@RequestParam List<Long> ids) {
		return ApiResponse.ok(userService.getUserList(ids));
	}
}
