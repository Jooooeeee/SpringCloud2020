package com.joe.consumer.order80.controller;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/consumer")
public class consumerController {

    public static final String HTTP_URL="http://localhost:8001/payment8001/";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/payment")
    public CommonResult<Payment> getPaymentById(@RequestParam("id") long id){
       return restTemplate.getForObject(HTTP_URL+"payment?id="+id,CommonResult.class);
    }

    @PostMapping("/create")
    public CommonResult<Integer> create(@RequestBody Payment payment){
        return restTemplate.postForObject(HTTP_URL+"create",payment,CommonResult.class);
    }
}
