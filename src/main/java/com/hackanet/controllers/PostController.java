package com.hackanet.controllers;

import com.hackanet.json.dto.PostDto;
import com.hackanet.json.forms.PostCreateForm;
import com.hackanet.json.forms.PostSearchForm;
import com.hackanet.json.forms.PostUpdateForm;
import com.hackanet.json.mappers.PostMapper;
import com.hackanet.models.Post;
import com.hackanet.models.User;
import com.hackanet.models.enums.PostImportance;
import com.hackanet.services.PostService;
import com.hackanet.services.PostServiceImpl;
import com.hackanet.services.PostViewService;
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
    private static final String GET_BY_IMPORTANCE = "/importance";
    private static final String CHANGE_IMPORTANCE = POST + GET_BY_IMPORTANCE;
    private static final String GET_LIKED_POSTS_FOR_USER = "/liked";

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostServiceImpl postService;
    @Autowired
    private PostViewService postViewService;

    @PostMapping(ADD)
    @ApiOperation(value = "Add new post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated() || hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
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
    @PreAuthorize("isAuthenticated() || hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        postService.delete(id, user);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = false, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get post by id")
    public ResponseEntity<PostDto> get(@PathVariable Long id,
                                       @AuthenticationPrincipal User user) {
        Post post = postService.get(id);
        postViewService.addView(user, post);
        return ResponseEntity.ok(postMapper.map(post));
    }

    @PutMapping(POST)
    @ApiOperation("Update post by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated() || hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PostDto> update(@PathVariable Long id,
                                          @Valid @RequestBody PostUpdateForm form,
                                          @AuthenticationPrincipal User user) {
        Post post = postService.update(id, user, form);
        return ResponseEntity.ok(postMapper.map(post));
    }

    @GetMapping(GET_BY_IMPORTANCE)
    @ApiOperation("Get by importance")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    public ResponseEntity<List<PostDto>> getByImportance(@RequestParam PostImportance importance) {
        List<Post> posts = postService.getByImportance(importance);
        return ResponseEntity.ok(postMapper.map(posts));
    }

    @PutMapping(CHANGE_IMPORTANCE)
    @ApiOperation("Update importance status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    public ResponseEntity<PostDto> updateImportance(@PathVariable Long id,
                                                    @RequestParam PostImportance postImportance) {
        Post post = postService.changePostImportance(id, postImportance);
        return ResponseEntity.ok(postMapper.map(post));
    }

    @PostMapping
    @ApiOperation("Search posts")
    public ResponseEntity<List<PostDto>> search(@RequestBody PostSearchForm form) {
        List<Post> posts = postService.postList(form);
        return ResponseEntity.ok(postMapper.map(posts));
    }

    @GetMapping(GET_LIKED_POSTS_FOR_USER)
    @ApiOperation("Get liked posts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PostDto>> getLikedPosts(@AuthenticationPrincipal User user) {
        List<Post> posts = postService.getLikedPostsForUser(user.getId());
        return ResponseEntity.ok(postMapper.map(posts));
    }
}
