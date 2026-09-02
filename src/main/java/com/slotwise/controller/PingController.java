package com.slotwise.controller;

import com.slotwise.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1")
public class PingController {
    
    @GetMapping("/health")
    public ApiResponse<String> health(){
        return ApiResponse.success("Slotwise is up and running");

    }


    @GetMapping("/ping")
    public ApiResponse<String> ping(){
        return ApiResponse.success("pong");
    }
    
}
