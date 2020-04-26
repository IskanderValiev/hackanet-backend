package com.hackanet.controllers;

import com.hackanet.json.dto.TeamDto;
import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamSearchForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.json.mappers.TeamMapper;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.services.team.TeamService;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@RestController
@Api(tags = "Team Controller")
@RequestMapping(TeamController.ROOT)
public class TeamController {

    public static final String ROOT = "/teams";

    private static final String CREATE = "/create";
    private static final String TEAM = "/{id}";
    private static final String SEARCH = "/search";
    private static final String DELETE_MEMBER = TEAM + "/members/delete";
    private static final String BY_USER = "/users";

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamService teamService;

    @PostMapping(CREATE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamDto> create(@Valid @RequestBody TeamCreateForm form,
                                          @AuthenticationPrincipal User user) {
        Team team = teamService.createTeam(user, form);
        return new ResponseEntity<>(teamMapper.map(team), HttpStatus.CREATED);
    }

    @PutMapping(TEAM)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamDto> update(@PathVariable Long id,
                                          @RequestBody TeamUpdateForm form,
                                          @AuthenticationPrincipal User user) {
        Team team = teamService.updateTeam(id, user, form);
        return new ResponseEntity<>(teamMapper.map(team), HttpStatus.OK);
    }

    @GetMapping(TEAM)
    public ResponseEntity<TeamDto> get(@PathVariable Long id) {
        Team team = teamService.get(id);
        return ResponseEntity.ok(teamMapper.map(team));
    }

    @PostMapping(SEARCH)
    public ResponseEntity<List<TeamDto>> search(@RequestBody TeamSearchForm form) {
        List<Team> teams = teamService.teamList(form);
        return ResponseEntity.ok(teamMapper.map(teams));
    }

    @DeleteMapping(DELETE_MEMBER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamDto> kickOutMember(@PathVariable Long id,
                                                 @RequestParam Long userId,
                                                 @AuthenticationPrincipal User user,
                                                 HttpServletRequest request) {
        Team team = teamService.deleteMember(id, userId, user);
        return ResponseEntity.ok(teamMapper.map(team));
    }

    @GetMapping(BY_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get the user's teams.",
            notes = "Relevant is nullable",
            response = Team.class,
            responseContainer = "List")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamDto>> getByUser(@AuthenticationPrincipal User user,
                                                   @RequestParam(required = false) Boolean relevant,
                                                   HttpServletRequest request) {
        final List<Team> teams = teamService.getTeamsByUser(user.getId(), relevant);
        return ResponseEntity.ok(teamMapper.map(teams));
    }
}

