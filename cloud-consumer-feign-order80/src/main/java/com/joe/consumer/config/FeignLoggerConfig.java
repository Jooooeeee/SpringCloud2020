package com.joe.consumer.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignLoggerConfig {

    @Bean
    public Logger.Level getBean(){
        return Logger.Level.FULL;
    }
}
