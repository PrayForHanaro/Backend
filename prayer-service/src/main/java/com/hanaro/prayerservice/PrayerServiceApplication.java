package com.hanaro.prayerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hanaro")
public class PrayerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrayerServiceApplication.class, args);
    }

}
