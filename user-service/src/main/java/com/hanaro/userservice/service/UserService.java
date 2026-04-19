package com.hanaro.userservice.service;

import com.hanaro.common.exception.ErrorCode;
import com.hanaro.userservice.domain.*;
import com.hanaro.userservice.dto.request.LoginRequestDTO;
import com.hanaro.userservice.dto.request.UsePointRequest;
import com.hanaro.userservice.client.OrgClient;
import com.hanaro.userservice.dto.request.SignUpRequestDTO;
import com.hanaro.userservice.dto.response.*;
import com.hanaro.userservice.exception.LoginFailException;
import com.hanaro.userservice.exception.UserNotFoundException;
import com.hanaro.userservice.mapper.UserMapper;
import com.hanaro.userservice.repository.PointRepository;
import com.hanaro.userservice.repository.UserRepository;
import com.hanaro.common.storage.StorageService;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public UserMyPageResponseDTO getMyPageInfo(Long userId){
      User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
      OrgMyPageResponseDTO orgDto = orgClient.getOrg();

      UserMyPageResponseDTO dto = UserMyPageResponseDTO.of(user, orgDto);
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


    public LoginResponseDTO verify(LoginRequestDTO request) {
        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> new LoginFailException());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailException();
        }

        return new LoginResponseDTO(
                user.getUserId().toString(),
                user.getName(),
                user.getRole(),
                user.getOrgId()
        );
    }
}