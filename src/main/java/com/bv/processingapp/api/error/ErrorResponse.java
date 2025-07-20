package com.bv.processingapp.api.error;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class ErrorResponse {
    Integer code;
    String description;
}
