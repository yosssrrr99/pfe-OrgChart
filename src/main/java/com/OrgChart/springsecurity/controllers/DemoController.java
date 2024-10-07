package com.OrgChart.springsecurity.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/demo")
public class DemoController {

    @GetMapping
    public ResponseEntity<String> sayHellp(){
        return ResponseEntity.ok("hello");
    }
}
