package com.hanaro.userservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "일회성 송금 간 필요한 유저 정보")
public class UserGivingOnceDTO {
	@NotNull
	private String name;

	@NotNull
	private BigDecimal maxPoint;

	@NotNull
	private String bankAccount;

	@NotNull
	private String churchName;
}
