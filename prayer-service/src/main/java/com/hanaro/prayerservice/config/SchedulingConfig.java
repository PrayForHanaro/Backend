package com.hanaro.prayerservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Scheduled job 활성화.
 * 실 job은 TransferScheduler(C-5) 등 후속 phase에서 추가.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
