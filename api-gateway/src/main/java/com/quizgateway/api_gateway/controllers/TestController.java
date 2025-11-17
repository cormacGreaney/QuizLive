package com.quizgateway.api_gateway.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


        @RestController
        public class TestController {
            @GetMapping("/test")
            public String testGateway() {
                return "API gateway is running...";

            }
        }
