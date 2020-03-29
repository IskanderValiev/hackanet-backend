package com.hackanet.controllers;

import com.hackanet.json.dto.SubscriptionDto;
import com.hackanet.json.mappers.SubscriptionMapper;
import com.hackanet.models.hackathon.Subscription;
import com.hackanet.models.User;
import com.hackanet.services.SubscriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/7/19
 */
@RestController
@Api(tags = "Subscription Controller")
@RequestMapping("/subscription")
public class SubscriptionController {

    private static final String SUBSCRIBE = "/subscribe";
    private static final String UNSUBSCRIBE = "/unsubscribe";
    private static final String BY_USER = "/users";

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @GetMapping(SUBSCRIBE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Subscribe to a hackathon")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDto> subscribe(@AuthenticationPrincipal User user,
                                                     @RequestParam("hackathonId") Long hackathonId) {
        Subscription subscribe = subscriptionService.subscribe(user, hackathonId);
        return ResponseEntity.ok(subscriptionMapper.map(subscribe));
    }

    @GetMapping(UNSUBSCRIBE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Unsubscribe to a hackathon")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unsubscribe(@AuthenticationPrincipal User user,
                                                     @RequestParam("hackathonId") Long hackathonId) {
        subscriptionService.unsubscribe(user, hackathonId);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(BY_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get subscriptions by user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SubscriptionDto>> getByUser(@AuthenticationPrincipal User user) {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptionsByUser(user);
        return ResponseEntity.ok(subscriptionMapper.map(subscriptions));
    }


}
