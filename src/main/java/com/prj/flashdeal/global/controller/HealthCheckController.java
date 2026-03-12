package com.prj.flashdeal.global.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/health/check")
    public String check() {
        try {
            return "Current Server IP: " + InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown Server";
        }
    }
}
