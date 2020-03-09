package com.hackanet.controllers;

import com.hackanet.json.dto.CommentLikeDto;
import com.hackanet.json.mappers.CommentLikeMapper;
import com.hackanet.models.CommentLike;
import com.hackanet.models.User;
import com.hackanet.models.enums.LikeType;
import com.hackanet.services.CommentLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
@RestController
@Api("Comment Like Controller")
@RequestMapping(CommentController.ROOT + "/likes")
public class CommentLikeController {

    private static final String BY_COMMENT = "/comment/{commentId}";
    private static final String ONE = "/{id}";

    @Autowired
    private CommentLikeService commentLikeService;

    @Autowired
    private CommentLikeMapper mapper;

    @PostMapping(BY_COMMENT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Like the comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentLikeDto> create(@PathVariable Long commentId,
                                                 @RequestParam LikeType type,
                                                 @AuthenticationPrincipal User user) {
        CommentLike commentLike = commentLikeService.create(user, commentId, type);
        return new ResponseEntity<>(mapper.map(commentLike), HttpStatus.CREATED);
    }

    @PutMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Update the like of the comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentLikeDto> update(@PathVariable Long commentId,
                                                 @RequestParam LikeType type,
                                                 @AuthenticationPrincipal User user) {
        CommentLike commentLike = commentLikeService.update(user, commentId, type);
        return new ResponseEntity<>(mapper.map(commentLike), HttpStatus.CREATED);
    }

    @PutMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = false, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get likes of the comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentLikeDto>> byComment(@PathVariable Long commentId) {
        List<CommentLike> commentLikes = commentLikeService.getByCommentId(commentId);
        return ResponseEntity.ok(mapper.map(commentLikes));
    }

    @DeleteMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Delete the like of the comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        commentLikeService.delete(user, id);
        return ResponseEntity.ok("OK");
    }
}
