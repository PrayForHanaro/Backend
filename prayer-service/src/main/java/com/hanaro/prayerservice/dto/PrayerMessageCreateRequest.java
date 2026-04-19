package com.hanaro.prayerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrayerMessageCreateRequest {

    @NotBlank(message = "메시지 내용은 비어있을 수 없습니다")
    @Size(max = 250, message = "메시지는 250자 이하여야 합니다")
    private String content;
}
