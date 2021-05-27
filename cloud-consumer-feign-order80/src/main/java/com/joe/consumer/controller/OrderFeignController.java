package com.joe.consumer.controller;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import com.joe.consumer.server.PaymentFeignServer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class OrderFeignController {

    private final PaymentFeignServer paymentFeignServer;

    @GetMapping(value = "/consumer/payment8001/payment")
    public CommonResult<Payment> payment(@RequestParam("id")Long id) {
       return this.paymentFeignServer.getPaymentById(id);
    }

    @GetMapping(value = "/consumer/payment8001/testFeign-timeout")
    public CommonResult<String> testFeignTimeout() {
        return this.paymentFeignServer.testFeignTimeout();
    }
}
