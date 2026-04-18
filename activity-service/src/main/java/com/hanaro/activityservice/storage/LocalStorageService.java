package com.hanaro.activityservice.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hanaro.activityservice.exception.ActivityErrorCode;
import com.hanaro.common.exception.BaseException;
import com.hanaro.common.storage.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("local")
@Primary
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

	private final String uploadDir = "uploads/";

	@Override
	public String upload(MultipartFile file, String directory) {
		try {
			String fileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename();
			Path path = Paths.get(uploadDir + directory + "/" + fileName);
			Files.createDirectories(path.getParent());
			Files.copy(file.getInputStream(), path);
			return "/uploads/" + directory + "/" + fileName;
		} catch (IOException e) {
			throw new BaseException(ActivityErrorCode.FILE_UPLOAD_FAILED);
		}
	}

	@Override
	public void delete(String fileUrl) {
	    try {
	        Files.deleteIfExists(Paths.get(fileUrl.substring(1)));
	    } catch (IOException e) {
	        throw new BaseException(ActivityErrorCode.FILE_DELETE_FAILED);
	    }
	}

	@Override
	public String getPresignedUrl(String fileUrl) {
	    return fileUrl;
	}

	}

