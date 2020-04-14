package com.hackanet.controllers;

import com.hackanet.models.user.User;
import com.hackanet.services.hackathon.HackathonLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/5/20
 */
@RestController
@Api(tags = "Hackathon Like Controller")
@RequestMapping("/hackathons/likes")
public class HackathonLikeController {

    private static final String HACKATHON = "/{id}";

    @Autowired
    private HackathonLikeService hackathonLikeService;

    @GetMapping(HACKATHON)
    @ApiOperation("Like a hackathon")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public String like(@PathVariable Long id,
                       @AuthenticationPrincipal User user,
                       HttpServletRequest request) {
        hackathonLikeService.like(user, id);
        return "String";
    }

    @DeleteMapping(HACKATHON)
    @ApiOperation("Unlike a hackathon")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public String unlike(@PathVariable Long id,
                       @AuthenticationPrincipal User user,
                       HttpServletRequest request) {
        hackathonLikeService.unlike(user, id);
        return "String";
    }
}
