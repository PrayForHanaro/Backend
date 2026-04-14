package com.hanaro.common.response;

import lombok.Getter;

/**
 * 공통 API 응답 형식
 * 모든 서비스 Controller에서 사용
 *
 * 성공: ApiResponse.ok(data)
 * 실패: ApiResponse.fail(errorCode)
 */
@Getter
public class ApiResponse<T> {

	/** 성공 여부 */
	private final boolean success;

	/** 응답 메시지 */
	private final String message;

	/** 응답 데이터 */
	private final T data;

	private ApiResponse(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	/** 데이터 있는 성공 응답 */
	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(true, "success", data);
	}

	/** 메시지 + 데이터 성공 응답 */
	public static <T> ApiResponse<T> ok(String message, T data) {
		return new ApiResponse<>(true, message, data);
	}

	/** 데이터 없는 성공 응답 */
	public static <T> ApiResponse<T> ok() {
		return new ApiResponse<>(true, "success", null);
	}

	/** 실패 응답 */
	public static <T> ApiResponse<T> fail(String message) {
		return new ApiResponse<>(false, message, null);
	}
}