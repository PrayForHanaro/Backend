package com.hanaro.prayerservice.service;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.prayerservice.client.user.UserClient;
import com.hanaro.prayerservice.client.user.dto.UserGivingResponse;
import com.hanaro.prayerservice.client.user.dto.WithdrawRequest;
import com.hanaro.prayerservice.domain.OnceTransfer;
import com.hanaro.prayerservice.domain.TransferStatus;
import com.hanaro.prayerservice.dto.OnceTransferRequest;
import com.hanaro.prayerservice.dto.OnceTransferResponse;
import com.hanaro.prayerservice.exception.PrayerErrorCode;
import com.hanaro.prayerservice.exception.PrayerException;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OnceTransferServiceTest {

    private static final Long SENDER_ID = 1L;
    private static final Long FROM_ACCOUNT_ID = 100L;

    @Mock
    private UserClient userClient;

    @Mock
    private OnceTransferStatusWriter statusWriter;

    @InjectMocks
    private OnceTransferService onceTransferService;

    private OnceTransferRequest request;

    @BeforeEach
    void setUp() {
        request = OnceTransferRequest.builder()
                .accountNumber("111-222-333")
                .amount(5000L)
                .message("축복합니다")
                .build();
    }

    @Test
    @DisplayName("기본 출금 계좌가 없으면 NO_DEFAULT_ACCOUNT 예외를 던진다")
    void send_withoutDefaultAccount_throws() {
        given(userClient.getGivingInfo()).willReturn(ApiResponse.ok(
                UserGivingResponse.builder().accountId(null).build()
        ));

        assertThatThrownBy(() -> onceTransferService.send(SENDER_ID, request))
                .isInstanceOf(PrayerException.class)
                .hasFieldOrPropertyWithValue("errorCode", PrayerErrorCode.NO_DEFAULT_ACCOUNT);

        verify(statusWriter, never()).createPending(any());
    }

    @Test
    @DisplayName("정상 송금: PENDING 저장 → withdraw → markSuccess 순서로 호출되고 status=SUCCESS 응답")
    void send_success_transitionsPendingToSuccess() {
        givenGivingInfo();
        OnceTransfer pending = pendingDraft(42L);
        given(statusWriter.createPending(any(OnceTransfer.class))).willReturn(pending);

        OnceTransferResponse response = onceTransferService.send(SENDER_ID, request);

        verify(statusWriter).createPending(any(OnceTransfer.class));
        verify(userClient).withdraw(eq(FROM_ACCOUNT_ID), any(WithdrawRequest.class));
        verify(statusWriter).markSuccess(eq(42L), any(Instant.class));
        verify(statusWriter, never()).markFailed(anyLong(), anyString());

        assertThat(response.getId()).isEqualTo("42");
        assertThat(response.getStatus()).isEqualTo(TransferStatus.SUCCESS.name());
        assertThat(response.getSentAt()).isEqualTo(pending.getSentAt());
    }

    @Test
    @DisplayName("FeignException 발생 시 markFailed 호출 후 TRANSFER_FAILED 예외로 래핑")
    void send_feignFailure_marksFailed() {
        givenGivingInfo();
        OnceTransfer pending = pendingDraft(7L);
        given(statusWriter.createPending(any(OnceTransfer.class))).willReturn(pending);
        willThrow(buildFeignException())
                .given(userClient).withdraw(anyLong(), any(WithdrawRequest.class));

        assertThatThrownBy(() -> onceTransferService.send(SENDER_ID, request))
                .isInstanceOf(PrayerException.class)
                .hasFieldOrPropertyWithValue("errorCode", PrayerErrorCode.TRANSFER_FAILED);

        verify(statusWriter).markFailed(eq(7L), anyString());
        verify(statusWriter, never()).markSuccess(anyLong(), any(Instant.class));
    }

    @Test
    @DisplayName("withdraw에서 예상치 못한 RuntimeException이 발생해도 markFailed 후 TRANSFER_FAILED로 래핑")
    void send_runtimeFailure_marksFailed() {
        givenGivingInfo();
        OnceTransfer pending = pendingDraft(11L);
        given(statusWriter.createPending(any(OnceTransfer.class))).willReturn(pending);
        willThrow(new RuntimeException("boom"))
                .given(userClient).withdraw(anyLong(), any(WithdrawRequest.class));

        assertThatThrownBy(() -> onceTransferService.send(SENDER_ID, request))
                .isInstanceOf(PrayerException.class)
                .hasFieldOrPropertyWithValue("errorCode", PrayerErrorCode.TRANSFER_FAILED);

        verify(statusWriter).markFailed(eq(11L), anyString());
        verify(statusWriter, never()).markSuccess(anyLong(), any(Instant.class));
    }

    private void givenGivingInfo() {
        given(userClient.getGivingInfo()).willReturn(ApiResponse.ok(
                UserGivingResponse.builder().accountId(FROM_ACCOUNT_ID).build()
        ));
    }

    private OnceTransfer pendingDraft(long id) {
        return OnceTransfer.builder()
                .onceTransferId(id)
                .senderId(SENDER_ID)
                .fromAccountId(FROM_ACCOUNT_ID)
                .toAccountNumber(request.getAccountNumber())
                .amount(BigDecimal.valueOf(request.getAmount()))
                .message(request.getMessage())
                .sentAt(Instant.now())
                .status(TransferStatus.PENDING)
                .build();
    }

    private static FeignException buildFeignException() {
        Request feignRequest = Request.create(
                Request.HttpMethod.POST,
                "http://user-service/internal/accounts/100/withdraw",
                new HashMap<>(),
                Request.Body.empty(),
                new RequestTemplate());
        return new FeignException.InternalServerError(
                "withdraw failed",
                feignRequest,
                "body".getBytes(StandardCharsets.UTF_8),
                new HashMap<>());
    }
}
