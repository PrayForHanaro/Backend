package com.hanaro.offeringservice.client.user;

import com.hanaro.offeringservice.dto.UsePointRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "user-service",
    url = "http://user-service:8083"
)
public interface UserClient {
    @PostMapping("/internal/users/points/use")
    void usePoint(@RequestBody UsePointRequest request);
}
