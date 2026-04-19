package com.hanaro.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	String upload(MultipartFile file, String directory);

	void delete(String fileUrl);

	String getPresignedUrl(String fileUrl);
}
