package com.hanaro.userservice.service;

import com.hanaro.userservice.domain.*;
import com.hanaro.userservice.dto.*;
import com.hanaro.userservice.dto.response.UserGivingResponseDTO;
import com.hanaro.userservice.dto.response.UserHomeResponseDTO;
import com.hanaro.userservice.dto.response.UserSimpleResponseDTO;
import com.hanaro.userservice.mapper.UserMapper;
import com.hanaro.userservice.repository.PointRepository;
import com.hanaro.userservice.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


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

}
