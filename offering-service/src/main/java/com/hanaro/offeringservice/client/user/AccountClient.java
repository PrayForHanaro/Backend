package com.hanaro.offeringservice.client.user;

import com.hanaro.offeringservice.dto.AccountWithdrawRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 보내는 쪽의 정보를 쓴다. client 는 받는쪽에 만든다.
@FeignClient(
    name = "user-service",
    url = "http://user-service:8080"
)
public interface AccountClient {

    @PostMapping("/internal/accounts/{accountId}/withdraw")
    void withdraw(@PathVariable Long accountId, @RequestBody AccountWithdrawRequest request);
}
