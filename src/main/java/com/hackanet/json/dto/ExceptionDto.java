package com.hackanet.json.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Rustem Galiev at 21:18, 25.02.19
 *
 * @author Rustem Galiev
 **/

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ExceptionDto {
    private String message;

    public static ExceptionDto of(String message) {
        return new ExceptionDto(message);
    }
}
