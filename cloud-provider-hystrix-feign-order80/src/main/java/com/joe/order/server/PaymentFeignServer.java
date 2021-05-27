package com.joe.order.server;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "CLOUD-PROVIDER-HYSTRIX-PAYMENT",fallback = PaymentFeignServerImpl.class )
public interface PaymentFeignServer {

    @GetMapping(value = "/payment8001/payment")
    CommonResult<Payment> getPaymentById(@RequestParam("id") Long id);

    @GetMapping(value = "/payment/timeout")
    CommonResult<String> testFeignTimeout();
}
