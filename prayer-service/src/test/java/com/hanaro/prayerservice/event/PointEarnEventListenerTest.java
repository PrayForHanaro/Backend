package com.hanaro.prayerservice.event;

import com.hanaro.prayerservice.domain.PointType;
import com.hanaro.prayerservice.kafka.PointEarnPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointEarnEventListenerTest {

    @Mock
    private PointEarnPublisher pointEarnPublisher;

    @InjectMocks
    private PointEarnEventListener listener;

    private final PointEarnEvent event = PointEarnEvent.builder()
            .userId(1L)
            .pointType(PointType.SAVINGS_JOIN)
            .amount(500L)
            .refId(10L)
            .info("test")
            .timestamp(Instant.now())
            .build();

    @Test
    @DisplayName("트랜잭션 커밋 이후 publisher로 이벤트를 위임한다")
    void onPointEarn_delegatesToPublisher() {
        listener.onPointEarn(event);

        verify(pointEarnPublisher).publish(event);
    }

    @Test
    @DisplayName("publisher에서 예외가 발생해도 호출자에게 전파하지 않는다")
    void onPointEarn_swallowsException() {
        willThrow(new RuntimeException("kafka down"))
                .given(pointEarnPublisher).publish(event);

        assertThatCode(() -> listener.onPointEarn(event))
                .doesNotThrowAnyException();
    }
}
