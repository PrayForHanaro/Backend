package com.hanaro.userservice.service;

import com.hanaro.userservice.domain.*;
import com.hanaro.userservice.dto.Request.UsePointRequest;
import com.hanaro.userservice.dto.response.UserGivingResponseDTO;
import com.hanaro.userservice.dto.response.UserHomeResponseDTO;
import com.hanaro.userservice.dto.response.UserSimpleResponseDTO;
import com.hanaro.userservice.domain.Point;
import com.hanaro.userservice.domain.PointType;
import com.hanaro.userservice.domain.User;
import com.hanaro.userservice.dto.UserGivingResponseDTO;
import com.hanaro.userservice.dto.UserHomeResponseDTO;
import com.hanaro.userservice.dto.UserSimpleResponseDTO;
import com.hanaro.userservice.dto.request.UsePointRequest;
import com.hanaro.userservice.mapper.UserMapper;
import com.hanaro.userservice.repository.PointRepository;
import com.hanaro.userservice.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
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
  public void usePoint(Long userId, UsePointRequest request) {

    User user = userRepository.findById(userId).orElseThrow();

    user.minusPoint(request.getAmount());

    Point point = Point.usePoint(user, request.getAmount());

    pointRepository.save(point);
  }

  public int getPointSum(Long userId){
    User user = userRepository.findById(userId).orElseThrow();
    return user.getPointSum();
    }
}
