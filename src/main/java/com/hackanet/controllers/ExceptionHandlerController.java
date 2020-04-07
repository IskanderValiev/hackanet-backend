package com.hackanet.controllers;

import com.hackanet.exceptions.*;
import com.hackanet.json.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@ControllerAdvice
public class ExceptionHandlerController {
    @ResponseBody
    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class, BadFormTypeException.class, AlreadyExistException.class})
    public ResponseEntity<ExceptionDto> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionDto.of(ex.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler({ForbiddenException.class, BlackListException.class})
    public ResponseEntity<ExceptionDto> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ExceptionDto.of(ex.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionDto.of(ex.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionDto> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ExceptionDto.of(ex.getMessage()));
    }


}
