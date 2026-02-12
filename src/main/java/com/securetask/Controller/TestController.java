package com.securetask.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    

    @GetMapping("/api/v1/test/secured")
    public String securedEndpoint() {
        return "This is a secured endpoint! v3";
    }

}
