package com.hanaro.userservice.service;

import com.hanaro.userservice.domain.*;
import com.hanaro.userservice.dto.request.UsePointRequest;
import com.hanaro.userservice.client.OrgClient;
import com.hanaro.userservice.dto.request.SignUpRequestDTO;
import com.hanaro.userservice.dto.response.OrgMyPageResponseDTO;
import com.hanaro.userservice.dto.response.UserGivingResponseDTO;
import com.hanaro.userservice.dto.response.UserHomeResponseDTO;
import com.hanaro.userservice.dto.response.UserMyPageResponseDTO;
import com.hanaro.userservice.dto.response.UserSimpleResponseDTO;
import com.hanaro.userservice.mapper.UserMapper;
import com.hanaro.userservice.repository.PointRepository;
import com.hanaro.userservice.repository.UserRepository;
import com.hanaro.common.storage.StorageService;

import java.util.List;
import java.util.stream.Collectors;

import com.hanaro.userservice.dto.request.LoginRequestDTO;
import com.hanaro.userservice.dto.response.LoginResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PointRepository pointRepository;
  private final UserMapper userMapper;
  private final OrgClient orgClient;
  private final PasswordEncoder passwordEncoder;
  private final StorageService storageService;

  @Transactional
  public void signUp(SignUpRequestDTO request) {

      if (userRepository.existsByPhone(request.getPhoneNumber())) {
        throw new IllegalArgumentException("이미 가입된 번호입니다.");
      }

      String encodedPassword = passwordEncoder.encode(request.getPassword());

      User user = User.builder()
          .name(request.getName())
          .birthDate(request.getBirth())
          .phone(request.getPhoneNumber())
          .password(encodedPassword)
          .build();

      userRepository.save(user);
    }

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

    public UserMyPageResponseDTO getMyPageInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserMyPageResponseDTO dto = UserMyPageResponseDTO.builder()
                .name(user.getName())
                .profileUrl(user.getProfileUrl())
                .orgName("소속 교회 없음")
                .pointSum(user.getPointSum())
                .build();

        if (user.getProfileUrl() != null) {
            dto.setProfileUrl(storageService.getPresignedUrl(user.getProfileUrl()));
        }

        return dto;
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

    @Transactional
    public void updateProfileImage(Long userId, String profileUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        user.updateProfileUrl(profileUrl);
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByPhone(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("전화번호 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("전화번호 또는 비밀번호가 올바르지 않습니다.");
        }

        return LoginResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole().name())
                .orgId(user.getOrgId())
                .build();
    }

}
