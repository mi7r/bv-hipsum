package com.bv.processingapp.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ComputationControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void text_endpoint_shouldResponseWith200WhileParameterIsCorrect() throws Exception {
        mockMvc.perform(
                get("/betvictor/text")
                    .param("p", "1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @Test
    void text_endpoint_shouldResponseWith400WhileParameterIsLowerThanOne() throws Exception {
        mockMvc.perform(
                get("/betvictor/text")
                    .param("p", "0")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void text_endpoint_shouldResponseWith400WhileParameterIsNotAIntegerType() throws Exception {
        mockMvc.perform(
                get("/betvictor/text")
                    .param("p", "asd")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void text_endpoint_shouldResponseWith400WhileParameterIsMissing() throws Exception {
        mockMvc.perform(
                get("/betvictor/text")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

}
