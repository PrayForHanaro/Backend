package com.hanaro.userservice.dto.request;

public record LoginRequestDTO(
        String phone,
        String password
) {}