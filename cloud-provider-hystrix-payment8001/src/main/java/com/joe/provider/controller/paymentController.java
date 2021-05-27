package com.joe.provider.controller;

import com.joe.commons.entities.CommonResult;
import com.joe.provider.service.PaymentServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@AllArgsConstructor
@Slf4j
public class paymentController {

    private final PaymentServiceImpl paymentService;

    @GetMapping("/ok")
    public String getOk(){
        String result = paymentService.paymentInfo_OK();
        log.info("result======="+result);
        return result;
    }

    @GetMapping("/timeout")
    public String getTimeOut(){
        String result = paymentService.paymentInfo_TimeOut();
        log.info("result======="+result);
        return result;
    }

    //服务熔断
    @GetMapping("/circuit-breaker")
    public String paymentCircuitBreaker(@RequestParam("id") Integer id){
        String s = paymentService.paymentCircuitBreaker(id);
        return s;

    }
}
