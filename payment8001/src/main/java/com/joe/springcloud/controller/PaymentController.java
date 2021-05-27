package com.joe.springcloud.controller;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import com.joe.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment8001")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostMapping("/create-payment")
    public CommonResult<Integer> create(@RequestBody Payment payment){
        int i = paymentService.create(payment);
        if (i>0){
            return new CommonResult<>(200,"插入数据库成功"+serverPort);
        }
        return new CommonResult<>(500,"插入数据库失败");
    }

    @GetMapping(value = "/payment")
    public CommonResult<Payment> getPaymentById(@RequestParam("id") Long id){
        Payment paymentById = this.paymentService.getPaymentById(id);
        if (ObjectUtils.isEmpty(paymentById)){
            return new CommonResult<>(500,"查询失败");
        }
        return new CommonResult<>(200,"查询成功"+serverPort,paymentById);
    }

    @GetMapping("/discovery-client")
    public Object discoveryClient(){
        List<String> services = this.discoveryClient.getServices();

        for (int i = 0; i < services.size(); i++) {
            log.info("services name:"+ services.get(i));
        }

        List<ServiceInstance> instances = this.discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        instances.forEach(entity -> {
            log.info(entity.getServiceId()+"\t"+entity.getHost()+"\t"+entity.getUri());
        });
        return this.discoveryClient;
    }

    @GetMapping("/testFeign-timeout")
    public CommonResult<String> testFeignTimeout() {
        try{
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new CommonResult<>(200,"8001");
    }

}
