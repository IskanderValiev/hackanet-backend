package com.hackanet.controllers;

import com.hackanet.json.dto.UserReviewDto;
import com.hackanet.json.forms.UserReviewCreateForm;
import com.hackanet.json.mappers.UserReviewMapper;
import com.hackanet.models.User;
import com.hackanet.models.UserReview;
import com.hackanet.services.UserReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/21/19
 */
@RestController
@Api(tags = "User Review Controller")
@RequestMapping("/users/reviews")
public class UserReviewController {

    private static final String REVIEW = "/{id}";

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private UserReviewService userReviewService;

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Leave a review")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserReviewDto> leave(@Valid @RequestBody UserReviewCreateForm form,
                                               @AuthenticationPrincipal User user) {
        UserReview review = userReviewService.createReview(user, form);
        return new ResponseEntity<>(userReviewMapper.map(review), HttpStatus.CREATED);
    }
}
