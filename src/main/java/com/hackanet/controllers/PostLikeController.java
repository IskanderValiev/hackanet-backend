package com.hackanet.controllers;

import com.hackanet.json.dto.PostLikeDto;
import com.hackanet.json.mappers.PostLikeMapper;
import com.hackanet.models.PostLike;
import com.hackanet.models.User;
import com.hackanet.services.PostLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/2/19
 */
@RestController
@RequestMapping("/likes/post")
@Api(tags = "Post Like Controller")
public class PostLikeController {

    private static final String ID = "/{id}";

    @Autowired
    private PostLikeService postLikeService;
    @Autowired
    private PostLikeMapper postLikeMapper;

    @GetMapping(ID)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostLikeDto> likePost(@PathVariable Long id,
                                                @AuthenticationPrincipal User user) {
        PostLike like = postLikeService.like(id, user);
        return new ResponseEntity<>(postLikeMapper.map(like), HttpStatus.CREATED);
    }

    @DeleteMapping(ID)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unlikePost(@PathVariable Long id,
                                             @AuthenticationPrincipal User user) {
        postLikeService.unlike(id, user);
        return ResponseEntity.ok("OK");
    }
}
