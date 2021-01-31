package com.joe.springcloud.controller;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import com.joe.springcloud.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment8001")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

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
}
