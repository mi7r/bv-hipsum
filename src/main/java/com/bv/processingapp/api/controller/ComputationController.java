package com.bv.processingapp.api.controller;

import com.bv.processingapp.api.model.ComputationResultResponse;
import com.bv.processingapp.service.ComputationService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("betvictor")
@RequiredArgsConstructor
public class ComputationController {

    private final ComputationService computationService;

    @GetMapping(value = "/text", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComputationResultResponse> text(
        @RequestParam() @Min(value = 1) final int p
    ) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(computationService.processText(p));
    }
}
