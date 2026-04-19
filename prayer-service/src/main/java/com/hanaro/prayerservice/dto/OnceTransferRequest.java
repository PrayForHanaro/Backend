package com.hanaro.prayerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnceTransferRequest {

    @NotBlank(message = "accountNumber는 필수입니다")
    private String accountNumber;

    @NotNull(message = "amount는 필수입니다")
    @Positive(message = "amount는 양수여야 합니다")
    private Long amount;

    @Size(max = 250, message = "메시지는 250자 이하여야 합니다")
    private String message;
}
