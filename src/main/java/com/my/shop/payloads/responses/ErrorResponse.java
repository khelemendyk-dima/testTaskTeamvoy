package com.my.shop.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private long timestamp;
    private String message;
}
