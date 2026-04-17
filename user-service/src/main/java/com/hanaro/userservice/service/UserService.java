package com.hanaro.userservice.service;

import com.hanaro.userservice.domain.*;
import com.hanaro.userservice.dto.*;
import com.hanaro.userservice.mapper.UserMapper;
import com.hanaro.userservice.repository.PointRepository;
import com.hanaro.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final UserMapper userMapper;

    public UserHomeResponseDTO getHomeInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserHomeResponseDTO(user);
}

    public UserGivingResponseDTO getGivingInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserGivingResponseDTO(user);
    }

    public List<UserSimpleResponseDTO> getUserList(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toUserSimpleResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processPoints(Long userId, int amount, Long refId, PointType pointType, boolean isEarn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int finalAmount = isEarn 
                ? (int) (amount * user.getDonationRate()) // 줄 때, 받을 때 전부 0.01 처럼 소수로 받음
                : amount;

        Point point = Point.builder()
                .user(user)
                .amount(isEarn ? finalAmount : -finalAmount)
                .pointType(pointType)
                .refId(refId)
                .build();
        pointRepository.save(point);
    }
}
