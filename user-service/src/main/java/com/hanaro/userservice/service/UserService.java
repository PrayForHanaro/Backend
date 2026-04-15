package com.hanaro.userservice.service;

import java.math.BigDecimal;
import java.util.List;

import com.hanaro.userservice.domain.Account;
import com.hanaro.userservice.domain.PointType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.userservice.domain.Point;
import com.hanaro.userservice.domain.User;
import com.hanaro.userservice.dto.UserHomeResponse;
import com.hanaro.userservice.dto.UserGivingResponse;
import com.hanaro.userservice.dto.UserSimpleResponse;
import com.hanaro.userservice.repository.PointRepository;
import com.hanaro.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PointRepository pointRepository;

	public UserHomeResponse getHomeInfo(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		int totalPoint = user.getPoints().stream().mapToInt(Point::getAmount).sum();
		return new UserHomeResponse(user.getName(), totalPoint, user.getOrgId());
	}

	public UserGivingResponse getGivingInfo(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		BigDecimal totalPoint = BigDecimal.valueOf(user.getPoints().stream().mapToInt(Point::getAmount).sum());
		
		Account account = user.getDefaultAccount();
		String accountNum = (account != null) ? account.getAccountNumber() : "계좌 없음";
		Long accountId = (account != null) ? account.getAccountId() : null;
		
		return new UserGivingResponse(user.getName(), totalPoint, accountNum, user.getOrgId(), accountId);
	}

	public List<UserSimpleResponse> getUserList(List<Long> ids) {
		return userRepository.findAllById(ids).stream()
				.map(u -> new UserSimpleResponse(u.getUserId(), u.getName(), u.getImageType()))
				.toList();
	}

	/** 포인트 사용 (차감) 로직 */
	@Transactional
	public void usePoints(Long userId, int amount, Long refId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		Point point = Point.builder()
				.user(user)
				.amount(-amount) // 차감은 음수로 저장
				.pointType(PointType.OFFERING_ONCE) // 헌금 시 포인트 사용 타입 (Enum 확인 필요)
				.refId(refId)
				.build();
		
		pointRepository.save(point);
	}
}
