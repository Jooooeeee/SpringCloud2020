package com.joe.order.server;

import com.joe.commons.entities.CommonResult;
import com.joe.commons.entities.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentFeignServerImpl implements PaymentFeignServer {

    @Override
    public CommonResult<Payment> getPaymentById(Long id) {
        return null;
    }

    @Override
    public CommonResult<String> testFeignTimeout() {
        return new CommonResult(500,"错误了");
    }
}
