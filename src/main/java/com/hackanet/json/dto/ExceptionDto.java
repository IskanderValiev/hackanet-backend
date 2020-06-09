package com.hackanet.json.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ExceptionDto {
    private String message;

    public static ExceptionDto of(String message) {
        return new ExceptionDto(message);
    }
}
