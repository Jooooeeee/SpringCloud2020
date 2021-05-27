package com.joe.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableHystrix
@EnableDiscoveryClient
public class ConsumerHystrixFeign80 {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerHystrixFeign80.class,args);
    }
}
