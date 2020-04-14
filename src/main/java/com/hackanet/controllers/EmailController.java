package com.hackanet.controllers;

import com.hackanet.services.EmailService;
import com.hackanet.services.user.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@RestController
@RequestMapping("/email")
@Api(tags = {"Email Controller"})
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<String> test(@RequestParam String email) {
        emailService.test(email);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String code) {
        userService.confirmEmail(code);
        return ResponseEntity.ok("OK");
    }
}
