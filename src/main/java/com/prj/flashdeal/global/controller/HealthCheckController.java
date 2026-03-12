package com.prj.flashdeal.global.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/health/check")
    public Map<String, String> check() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("serverId", resolveServerId());
        response.put("hostname", resolveHostName());

        try {
            response.put("containerIp", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            response.put("containerIp", "unknown");
        }

        return response;
    }

    private String resolveServerId() {
        String serverId = System.getenv("SERVER_ID");
        if (serverId != null && !serverId.isBlank()) {
            return serverId;
        }
        return resolveHostName();
    }

    private String resolveHostName() {
        String hostName = System.getenv("HOSTNAME");
        if (hostName != null && !hostName.isBlank()) {
            return hostName;
        }

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
