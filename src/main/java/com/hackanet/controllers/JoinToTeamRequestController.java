package com.hackanet.controllers;

import com.hackanet.json.dto.JoinToTeamRequestDto;
import com.hackanet.json.forms.JoinToTeamRequestCreateForm;
import com.hackanet.json.mappers.JoinToTeamRequestMapper;
import com.hackanet.models.JoinToTeamRequest;
import com.hackanet.models.User;
import com.hackanet.models.enums.JoinToTeamRequestStatus;
import com.hackanet.repositories.JoinToTeamRequestService;
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
 * on 11/1/19
 */
@RestController
@RequestMapping("/teams/join/request")
@Api(tags = "Join To Team Request Controller")
public class JoinToTeamRequestController {

    private static final String CREATE = "/create";
    private static final String REQUEST = "/{id}";
    private static final String BY_TEAM = "/team/{id}";

    @Autowired
    private JoinToTeamRequestService joinToTeamRequestService;
    @Autowired
    private JoinToTeamRequestMapper joinToTeamRequestMapper;

    @PostMapping(CREATE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Create join request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JoinToTeamRequestDto> create(@RequestBody JoinToTeamRequestCreateForm form,
                                                       @AuthenticationPrincipal User user) {
        JoinToTeamRequest request = joinToTeamRequestService.create(user, form);
        return new ResponseEntity<>(joinToTeamRequestMapper.map(request), HttpStatus.CREATED);
    }

    @DeleteMapping(REQUEST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Delete join request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        joinToTeamRequestService.delete(user, id);
        return ResponseEntity.ok("OK");
    }

    @PutMapping(REQUEST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Update status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JoinToTeamRequestDto> updateStatus(@RequestParam JoinToTeamRequestStatus status,
                                                             @PathVariable Long id,
                                                             @AuthenticationPrincipal User user) {
        JoinToTeamRequest request = joinToTeamRequestService.updateStatus(user, id, status);
        return ResponseEntity.ok(joinToTeamRequestMapper.map(request));
    }

    @GetMapping(BY_TEAM)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get all requests by team id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<JoinToTeamRequestDto>> getByTeam(@PathVariable Long id,
                                                                @AuthenticationPrincipal User user) {
        List<JoinToTeamRequest> requests = joinToTeamRequestService.getByTeamId(user, id);
        return ResponseEntity.ok(joinToTeamRequestMapper.map(requests));
    }
}
