package com.hanaro.prayerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.hanaro")
@EnableFeignClients(basePackages = "com.hanaro.prayerservice.client")
public class PrayerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrayerServiceApplication.class, args);
    }

}
