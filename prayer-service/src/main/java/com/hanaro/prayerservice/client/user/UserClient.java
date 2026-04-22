package com.hanaro.prayerservice.client.user;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.prayerservice.client.user.dto.UserGivingResponse;
import com.hanaro.prayerservice.client.user.dto.WithdrawRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service",
        url = "http://user-service:8080"
)
public interface UserClient {

    @GetMapping("/internal/users/me/givingOnce")
    ApiResponse<UserGivingResponse> getGivingInfo();

    @PostMapping("/internal/accounts/{accountId}/withdraw")
    void withdraw(
            @PathVariable("accountId") Long accountId,
            @RequestBody WithdrawRequest request);
}
