package com.hackanet.controllers;

import com.hackanet.json.dto.CommentDto;
import com.hackanet.json.forms.CommentCreateForm;
import com.hackanet.json.forms.CommentUpdateForm;
import com.hackanet.json.mappers.CommentMapper;
import com.hackanet.models.comment.Comment;
import com.hackanet.models.user.User;
import com.hackanet.services.comment.CommentService;
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

import javax.validation.Valid;
import java.util.List;

import static com.hackanet.controllers.CommentController.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
@RestController
@Api(tags = "Comment Controller")
@RequestMapping(ROOT)
public class CommentController {

    public static final String ROOT = "/comments";
    private static final String ONE = "/{id}";
    private static final String BY_POST = "/posts/{postId}";

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentMapper commentMapper;

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Add new comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentCreateForm form,
                                             @AuthenticationPrincipal User user) {
        Comment comment = commentService.create(user, form);
        return new ResponseEntity<>(commentMapper.map(comment), HttpStatus.CREATED);
    }

    @PutMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Update the comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> update(@PathVariable Long id,
                                             @Valid @RequestBody CommentUpdateForm form,
                                             @AuthenticationPrincipal User user) {
        Comment comment = commentService.update(user, id, form);
        return ResponseEntity.ok(commentMapper.map(comment));
    }

    @DeleteMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Delete the comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        commentService.delete(user, id);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(BY_POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = false, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get comments by the post")
    public ResponseEntity<List<CommentDto>> byPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getByPost(postId);
        return ResponseEntity.ok(commentMapper.map(comments));
    }
}
