package com.hackanet.controllers;

import com.hackanet.json.dto.PostDto;
import com.hackanet.json.forms.PostCreateForm;
import com.hackanet.json.forms.PostUpdateForm;
import com.hackanet.json.mappers.PostMapper;
import com.hackanet.models.Post;
import com.hackanet.models.User;
import com.hackanet.services.PostService;
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

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
@RestController
@Api(tags = "Post Controller")
@RequestMapping("/posts")
public class PostController {

    private static final String ADD = "/add";
    private static final String BY_HACKATHON = "/hackathon/{id}";
    private static final String BY_USER = "/user/{id}";
    private static final String POST = "/{id}";

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostService postService;

    @PostMapping(ADD)
    @ApiOperation(value = "Add new post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PostDto> add(@Valid @RequestBody PostCreateForm form,
                                       @AuthenticationPrincipal User user) {
        Post post = postService.add(form, user);
        return new ResponseEntity<>(postMapper.map(post), HttpStatus.CREATED);
    }

    @GetMapping(BY_HACKATHON)
    @ApiOperation(value = "Get posts by hackathon")
    public ResponseEntity<List<PostDto>> getByHackathon(@PathVariable Long id) {
        List<Post> posts = postService.getByHackathon(id);
        return ResponseEntity.ok(postMapper.map(posts));
    }

    @GetMapping(BY_USER)
    @ApiOperation(value = "Get posts by user")
    public ResponseEntity<List<PostDto>> getByUser(@PathVariable Long id) {
        List<Post> posts = postService.getByUser(id);
        return ResponseEntity.ok(postMapper.map(posts));
    }

    @DeleteMapping(POST)
    @ApiOperation(value = "Delete the post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        postService.delete(id, user);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(POST)
    @ApiOperation("Get post by id")
    public ResponseEntity<PostDto> get(@PathVariable Long id) {
        Post post = postService.get(id);
        return ResponseEntity.ok(postMapper.map(post));
    }

    @PutMapping(POST)
    @ApiOperation("Update post by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PostDto> update(@PathVariable Long id,
                                          @Valid @RequestBody PostUpdateForm form,
                                          @AuthenticationPrincipal User user) {
        Post post = postService.update(id, user, form);
        return ResponseEntity.ok(postMapper.map(post));
    }
}
