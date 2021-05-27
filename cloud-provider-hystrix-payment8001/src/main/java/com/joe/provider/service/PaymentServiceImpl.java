package com.joe.provider.service;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentServiceImpl {

    public String paymentInfo_OK() {
        return "线程池： " + Thread.currentThread().getName()
                + "   paymentInfo_OK,id:" + 31 + " 正常访问！";
    }

    @HystrixCommand(fallbackMethod = "paymentInfoTimeoutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")
    })
    public String paymentInfo_TimeOut() {

        try{
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "线程池： " + Thread.currentThread().getName()
                + "   paymentInfo_TimeOut,id:" + 31 + " 正常访问！";
    }

    public String paymentInfoTimeoutHandler() {
        return "线程池："+Thread.currentThread().getName()+" 系统繁忙，请稍后再试！id:";
    }


    //服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerFallback",commandProperties={
            @HystrixProperty(name = "circuitBreaker.enabled",value="true"),//是否启用熔断器
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "5000"),//窗口休眠器，即熔断后多久才尝试恢复调用链路
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),//最近的调用次数
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "6")//失败率占最近的调用次数的比例
    })
    public String paymentCircuitBreaker(Integer id) {
        if (id<0) {
            throw new RuntimeException("*********id,不能为负数");
        }
        String uuid = UUID.randomUUID().toString();
        return Thread.currentThread().getName() + "\t" +"调用成功，流水号："+uuid;
    }

    public String paymentCircuitBreakerFallback(Integer id) {
        return "id 不能为负数，请稍后再试，id："+id;
    }

}
