package com.hanaro.prayerservice.client.user;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.prayerservice.client.user.dto.UserGivingResponse;
import com.hanaro.prayerservice.client.user.dto.WithdrawRequest;
import com.hanaro.prayerservice.client.user.dto.WithdrawResponse;
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

    @GetMapping("/apis/user/users/me/givingOnce")
    ApiResponse<UserGivingResponse> getGivingInfo();

    // TODO: Bin0917 확정 후 경로·body 교체 (2026-04-17-user-api-for-bless.md §2)
    @PostMapping("/internal/accounts/{accountId}/withdraw")
    ApiResponse<WithdrawResponse> withdraw(
            @PathVariable("accountId") Long accountId,
            @RequestBody WithdrawRequest request);
}
