package com.hanaro.userservice.dto.response;

import com.hanaro.common.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long userId;
    private String name;
    private UserRole role;
    private Long orgId;
}