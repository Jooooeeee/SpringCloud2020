package config.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequestMapping("/")
@RequestScope
public class ConfigClientController {

    @Value("${config.info}")
    private String value;

    @GetMapping("/config-info")
    public String getConfigInfo() {
        return value;
    }

}
