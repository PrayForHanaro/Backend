package com.hanaro.userservice.service;

import com.hanaro.common.event.ActivityChurchPointEvent;
import com.hanaro.common.event.ActivityVolunteerPointEvent;
import com.hanaro.common.event.OfferingOncePointEvent;
import com.hanaro.common.event.OfferingRecurringPointEvent;
import com.hanaro.common.event.SavingsJoinPointEvent;
import com.hanaro.common.event.SavingsRecurringPointEvent;
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

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void signUp(SignUpRequestDTO request) {

        if (userRepository.existsByPhone(request.getPhone())) {
          throw new IllegalArgumentException("이미 가입된 번호입니다.");
        }

        // 비밀번호 해시
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
            .name(request.getName())
            .birthDate(request.getBirth())
            .phone(request.getPhone())
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

      return UserMyPageResponseDTO.of(user, orgDto);
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
  @Transactional
  public void processOfferingOnce(OfferingOncePointEvent event) {

    User user = findUser(event.getUserId());

    Point point = Point.createOfferingOnce(
        user,
        user.getDonationRate(),
        event.getDonationAmount().longValue(),
        event.getOfferingType()
    );

    savePoint(user, point);
  }

  @Transactional
  public void processOfferingRecurring(OfferingRecurringPointEvent event) {

    User user = findUser(event.getUserId());

    Point point = Point.builder()
        .user(user)
        .pointType(PointType.OFFERING_RECURRING)
        .amount(500)
        .info("정기헌금 등록")
        .build();

    savePoint(user, point);
  }

  @Transactional
  public void processActivityChurch(ActivityChurchPointEvent event) {

    User user = findUser(event.getUserId());

    Point point = Point.createActivityChurch(
        user,
        event.getTitle()
    );

    savePoint(user, point);
  }

  @Transactional
  public void processActivityVolunteer(ActivityVolunteerPointEvent event) {

    User user = findUser(event.getUserId());

    Point point = Point.createActivityVolunteer(
        user,
        event.getTitle()
    );

    savePoint(user, point);
  }

  @Transactional
  public void processSavingsJoin(SavingsJoinPointEvent event) {

    User user = findUser(event.getUserId());

    Point point = Point.createSavingsJoin(
        user,
        event.getProductName(),
        event.getTargetName()
    );

    savePoint(user, point);
  }

  @Transactional
  public void processSavingsRecurring(SavingsRecurringPointEvent event) {

    User user = findUser(event.getUserId());

    Point point = Point.createSavingsRecurring(
        user,
        event.getTargetName(),
        event.getPointAmount()
    );

    savePoint(user, point);
  }

  private User findUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
  }

  private void savePoint(User user, Point point) {
    pointRepository.save(point);
    user.addPoint(point.getAmount());
  }
}
