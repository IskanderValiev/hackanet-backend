package com.hackanet.controllers;

import com.hackanet.json.dto.TeamDto;
import com.hackanet.json.mappers.TeamMapper;
import com.hackanet.models.Team;
import com.hackanet.models.User;
import com.hackanet.services.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/25/19
 */
@RestController
@Api(tags = "Team Suggestion Controller")
@RequestMapping(TeamController.ROOT + "/suggestions")
public class TeamSuggestionController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMapper teamMapper;

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Change status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamDto>> getSuggestions(@AuthenticationPrincipal User user) {
        List<Team> teams = teamService.getTeamsSuggestion(user);
        return ResponseEntity.ok(teamMapper.map(teams));
    }


}
