package com.hackanet.controllers;

import com.hackanet.json.dto.TeamDto;
import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamSearchForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.json.mappers.TeamMapper;
import com.hackanet.models.Team;
import com.hackanet.models.User;
import com.hackanet.services.SkillCombinationService;
import com.hackanet.services.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
}

