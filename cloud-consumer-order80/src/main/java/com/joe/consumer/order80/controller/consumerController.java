package com.joe.consumer.order80.controller;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import com.joe.consumer.order80.lb.LoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consumer")
public class consumerController {

//    public static final String HTTP_URL="http://localhost:8001/payment8001/";

    public static final String HTTP_URL = "http://CLOUD-PAYMENT-SERVICE/payment8001/";

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private LoadBalancer loadBalancer;

    @GetMapping("/payment")
    public CommonResult<Payment> getPaymentById(@RequestParam("id") long id){
       return restTemplate.getForObject(HTTP_URL+"payment?id="+id,CommonResult.class);
    }

    @PostMapping("/create")
    public CommonResult<Integer> create(@RequestBody Payment payment){
        return restTemplate.postForObject(HTTP_URL+"create",payment,CommonResult.class);
    }

    @GetMapping("/payment-lb")
    public String getPaymentLb() {
        List<ServiceInstance> instances = this.discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        ServiceInstance instance = loadBalancer.instance(instances);
        if (instance == null) {
            throw new RuntimeException("没有可用服务");
        }
        return instance.getPort()+"";
    }
}
