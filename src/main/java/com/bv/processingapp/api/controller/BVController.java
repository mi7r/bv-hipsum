package com.bv.processingapp.api.controller;

import com.bv.processingapp.api.model.ProcessingResponse;
import com.bv.processingapp.service.TextProcessingService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class BVController {

    private final TextProcessingService textProcessingService;

    @GetMapping(value = "/text", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProcessingResponse> text(
        @RequestParam() @Min(value = 1) final int p
    ) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(textProcessingService.processText(p));
    }
}
