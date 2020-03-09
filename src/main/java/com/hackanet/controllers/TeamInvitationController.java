package com.hackanet.controllers;

import com.hackanet.json.dto.TeamInvitationDto;
import com.hackanet.json.mappers.TeamInvitationMapper;
import com.hackanet.models.team.TeamInvitation;
import com.hackanet.models.User;
import com.hackanet.models.enums.TeamInvitationStatus;
import com.hackanet.services.team.TeamInvitationService;
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
 * on 11/25/19
 */
@RestController
@Api(tags = "Team Invitation Controller")
@RequestMapping(TeamController.ROOT + "/invitations")
public class TeamInvitationController {

    private static final String CREATE = "/create";
    private static final String BY_USER = "/user";
    private static final String ONE = "/{id}";
    private static final String STATUS = ONE + "/status";

    @Autowired
    private TeamInvitationService teamInvitationService;

    @Autowired
    private TeamInvitationMapper teamInvitationMapper;

    @PostMapping(CREATE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Invite a user to the team")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamInvitationDto> create(@RequestParam("user_id") Long userId,
                                                    @RequestParam("team_id") Long teamId,
                                                    @AuthenticationPrincipal User user) {
        TeamInvitation invitation = teamInvitationService.createIfNotExists(user, userId, teamId);
        return new ResponseEntity<>(teamInvitationMapper.map(invitation), HttpStatus.CREATED);
    }

    @GetMapping(BY_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get all invitations by user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamInvitationDto>> getByUser(@AuthenticationPrincipal User user) {
        List<TeamInvitation> invitations = teamInvitationService.getAllByUser(user);
        return ResponseEntity.ok(teamInvitationMapper.map(invitations));
    }

    @GetMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get info securely")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamInvitationDto> info(@PathVariable("id") Long id,
                                                  @AuthenticationPrincipal User user) {
        TeamInvitation invitation = teamInvitationService.getInfoSecurely(user, id);
        return ResponseEntity.ok(teamInvitationMapper.map(invitation));
    }

    @GetMapping(STATUS)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Change status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamInvitationDto> changeStatus(@PathVariable("id") Long id,
                                                          @RequestParam TeamInvitationStatus status,
                                                          @AuthenticationPrincipal User user) {
        TeamInvitation invitation = teamInvitationService.changeStatus(user, id, status);
        return ResponseEntity.ok(teamInvitationMapper.map(invitation));
    }
}
