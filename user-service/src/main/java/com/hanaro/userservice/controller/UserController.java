package com.hanaro.userservice.controller;

import com.hanaro.userservice.dto.request.SignUpRequestDTO;
import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.common.storage.StorageService;
import com.hanaro.userservice.dto.request.SignUpRequestDTO;
import com.hanaro.userservice.dto.response.UserGivingResponseDTO;
import com.hanaro.userservice.dto.response.UserHomeResponseDTO;
import com.hanaro.userservice.dto.response.UserMyPageResponseDTO;
import com.hanaro.userservice.dto.response.UserSimpleResponseDTO;
import com.hanaro.userservice.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "유저", description = "유저 정보 핸들링")
@RestController
@RequestMapping("/apis/user/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final StorageService storageService;

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


	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> signUp(@RequestBody SignUpRequestDTO request) {
		userService.signUp(request);
		return ApiResponse.ok();
	}

  //로그아웃

	//이미지 수정 - presignedUrl 발급해주기
	//이미지 수정 - 수정한 url 저장
	@PostMapping("/profile-image")
	public ApiResponse<String> uploadProfileImage(
			@AuthenticationPrincipal CustomUserDetails user,
			@RequestPart("file") MultipartFile file
	) {
		String url = storageService.upload(file, "profile");
		userService.updateProfileImage(user.getUserId(), url);
		return ApiResponse.ok("프로필 이미지 수정이 완료되었습니다.", url);
	}

  //사용가능 포인트 조회
  @GetMapping("/point")
  public ApiResponse<Integer> getAvailablePoint(@AuthenticationPrincipal CustomUserDetails user) {
		return ApiResponse.ok(userService.getPointSum(user.getUserId()));  }

	@GetMapping("/me")
	public ApiResponse<UserMyPageResponseDTO> getMyPage(
			@AuthenticationPrincipal CustomUserDetails user
	) {
		return ApiResponse.ok(userService.getMyPageInfo(user.getUserId()));
	}

}
