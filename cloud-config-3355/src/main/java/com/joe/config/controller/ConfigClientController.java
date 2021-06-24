package com.joe.config.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ConfigClientController {

    @Value("${config.info}")
    private String value;

    @GetMapping("/config-info")
    public String getConfigInfo() {
        return value;
    }

}
