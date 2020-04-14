package com.hackanet.controllers;

import com.hackanet.json.dto.ConnectionInvitationDto;
import com.hackanet.json.mappers.ConnectionInvitationMapper;
import com.hackanet.models.ConnectionInvitation;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.ConnectionInvitationStatus;
import com.hackanet.services.ConnectionInvitationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/3/19
 */
@RestController
@Api(tags = "Connection Invitation Controller")
@RequestMapping("/connections/invitations")
public class ConnectionInvitationController {

    private static final String ONE = "/{id}";
    private static final String CHANGE_STATUS = ONE + "/status";
    private static final String BY_INVITED_USER = "/by/invited/user";
    private static final String INVITE = "/invite";

    @Autowired
    private ConnectionInvitationService connectionInvitationService;

    @Autowired
    private ConnectionInvitationMapper connectionInvitationMapper;

    @GetMapping(CHANGE_STATUS)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Change the invitation status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConnectionInvitationDto> changeStatus(@PathVariable("id") Long id,
                                                                @AuthenticationPrincipal User user,
                                                                @RequestParam("status")ConnectionInvitationStatus status) {
        ConnectionInvitation connectionInvitation = connectionInvitationService.changeStatus(user, id, status);
        return ResponseEntity.ok(connectionInvitationMapper.map(connectionInvitation));
    }

    @GetMapping(BY_INVITED_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get all invitations by invited user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ConnectionInvitationDto>> getByInvitedUser(@AuthenticationPrincipal User user) {
        Set<ConnectionInvitation> connectionInvitations = connectionInvitationService.getByInvitedUser(user.getId());
        List<ConnectionInvitationDto> map = connectionInvitationMapper.map(connectionInvitations);
        return ResponseEntity.ok(map);
    }

    @GetMapping(INVITE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Send invitation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConnectionInvitationDto> sendInvitation(@AuthenticationPrincipal User user,
                                                                  @RequestParam("invitedUserId") Long id) {
        ConnectionInvitation invitation = connectionInvitationService.sendInvitation(user, id);
        return ResponseEntity.ok(connectionInvitationMapper.map(invitation));
    }
}
