package com.hanaro.prayerservice.dto;

import com.hanaro.prayerservice.domain.GiftReceiverType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsJoinRequest {

    @NotNull(message = "receiverId는 필수입니다")
    @Positive(message = "receiverId는 양수여야 합니다")
    private Long receiverId;

    @NotNull(message = "giftReceiverType은 필수입니다")
    private GiftReceiverType giftReceiverType;

    @NotNull(message = "amount는 필수입니다")
    @Positive(message = "amount는 양수여야 합니다")
    private BigDecimal amount;

    @Min(value = 1, message = "transferDay는 1 이상이어야 합니다")
    @Max(value = 31, message = "transferDay는 31 이하여야 합니다")
    private int transferDay;

    @Positive(message = "goalDays는 양수여야 합니다")
    private int goalDays;
}
