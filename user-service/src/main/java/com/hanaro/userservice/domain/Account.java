package com.hanaro.userservice.domain;

import com.hanaro.common.exception.BaseException;
import com.hanaro.userservice.exception.UserErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Account extends  BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", nullable = true)
	private User user;

	@Column(name = "bankName", nullable = false, length = 50)
	private String bankName;

	@Column(name = "accountNumber", nullable = false, unique = true, length = 30)
	private String accountNumber;

	/** 잔액 */
	@Column(name = "balance", nullable = false)
	@Builder.Default
	private BigDecimal balance = BigDecimal.ZERO;

	@Column(name = "isHana", nullable = false)
	private boolean isHana;

	@Column(name = "isDefault", nullable = false)
	private boolean isDefault;

	@Column(name = "isSavings", nullable = false)
	private boolean isSavings;

	@Version
	@Column(name = "version")
	private Long version;

	public void setAsDefault() {
		this.isDefault = true;
	}

	public void unsetDefault() {
		this.isDefault = false;
	}

	/** 잔액 차감 */
	public void withdraw(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("유효하지 않은 출금 금액입니다.");
		}
		if (this.balance.compareTo(amount) < 0) {
			throw new BaseException(UserErrorCode.INSUFFICIENT_BALANCE);
		}
		this.balance = this.balance.subtract(amount);
	}
}
