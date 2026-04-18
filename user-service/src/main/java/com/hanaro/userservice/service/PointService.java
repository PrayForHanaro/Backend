package com.hanaro.userservice.service;

import com.hanaro.userservice.domain.Point;
import com.hanaro.userservice.domain.User;
import com.hanaro.userservice.dto.response.PageResponseDTO;
import com.hanaro.userservice.dto.response.PointResponseDTO;
import com.hanaro.userservice.repository.PointRepository;
import com.hanaro.userservice.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
  private final PointRepository pointRepository;

  @Transactional(readOnly = true)
  public PageResponseDTO<PointResponseDTO> getPointList(Long userId, Pageable pageable) {

    Page<Point> pointPage = pointRepository.findByUser_UserId(userId, pageable);

    Page<PointResponseDTO> dtoPage = pointPage.map(PointResponseDTO::from);

    return PageResponseDTO.from(dtoPage);  }
}
