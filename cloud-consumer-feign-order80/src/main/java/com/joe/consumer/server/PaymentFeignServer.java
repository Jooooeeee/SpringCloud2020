package com.joe.consumer.server;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "CLOUD-PAYMENT-SERVICE",path = "/payment8001")
public interface PaymentFeignServer {

    @GetMapping(value = "/payment8001/payment")
    CommonResult<Payment> getPaymentById(@RequestParam("id") Long id);

    @GetMapping("/testFeign-timeout")
    CommonResult<String> testFeignTimeout();
}
