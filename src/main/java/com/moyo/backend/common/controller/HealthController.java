package com.moyo.backend.common.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Hidden
    @GetMapping({"/", "/health"})
    public String health(){
        return "Server Status : UP";
    }
}
