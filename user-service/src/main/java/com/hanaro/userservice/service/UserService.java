package com.hanaro.userservice.service;

import java.math.BigDecimal;

import com.hanaro.userservice.client.OrgServiceClient;
import com.hanaro.userservice.dto.OrgResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.hanaro.userservice.domain.Point;
import com.hanaro.userservice.domain.User;
import com.hanaro.userservice.dto.UserGivingOnceDTO;
import com.hanaro.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final OrgServiceClient orgServiceClient;

	public UserGivingOnceDTO getGivingOnceUserInfo(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

		// org-service를 통해 교회 정보 가져오기
		OrgResponse org = orgServiceClient.getOrg(user.getOrgId());

		return UserGivingOnceDTO.builder()
			.name(user.getName())
			.bankAccount(user.getDefaultAccount() != null ? user.getDefaultAccount().getAccountNumber() : "N/A")
			.maxPoint(BigDecimal.valueOf(user.getPoints().stream().mapToInt(Point::getAmount).sum()))
			.churchName(org.getOrgName())
			.build();
	}
}
