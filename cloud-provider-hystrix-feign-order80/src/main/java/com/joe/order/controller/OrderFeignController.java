package com.joe.order.controller;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import com.joe.order.server.PaymentFeignServer;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@DefaultProperties(defaultFallback = "paymentInfoTimeoutHandler")
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


    @GetMapping(value = "/consumer/payment8001/payment-info")
    @HystrixCommand
    public String paymentInfo() {
        int i =10/0;
        return "正常运行";

    }

    public String paymentInfoTimeoutHandler() {
        return "线程池："+Thread.currentThread().getName()+" 系统繁忙，请稍后再试！id:";
    }
}
