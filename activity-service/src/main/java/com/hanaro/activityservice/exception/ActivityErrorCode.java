package com.hanaro.activityservice.exception;

import com.hanaro.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ActivityErrorCode implements ErrorCode {

	FILE_UPLOAD_FAILED("A001", "파일 업로드에 실패했습니다.", 500),
	FILE_DELETE_FAILED("A002", "파일 삭제에 실패했습니다.", 500);

	private final String code;
	private final String message;
	private final int status;

	ActivityErrorCode(String code, String message, int status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}