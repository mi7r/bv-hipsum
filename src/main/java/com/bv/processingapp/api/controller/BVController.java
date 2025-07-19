package com.bv.processingapp.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BVController {

    @GetMapping("/text")
    public ResponseEntity<String> text(){
        return ResponseEntity.ok("Hello World");
    }
}
