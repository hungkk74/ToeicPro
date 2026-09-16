package com.toeic.exam.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "subscriptionservice")
public interface SubscriptionClient {
    @GetMapping("/api/subscriptions/user/{userId}/active")
    ResponseEntity<Boolean> isUserSubscriptionActive(@PathVariable("userId") String userId);

}
