package com.example.securegatewaybackendservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Secure Backend Service!";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint. Anyone can access it!";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "This is a PRIVATE endpoint. You need to be authenticated to see this!";
    }
}