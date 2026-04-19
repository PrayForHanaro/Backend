package com.hanaro.userservice.dto.response;

import com.hanaro.common.auth.UserRole;

import java.util.List;

public record LoginResponseDTO(
        String userId,
        String name,
        UserRole role,
        Long orgId
) {}