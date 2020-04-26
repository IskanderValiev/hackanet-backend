package com.hackanet.controllers;

import com.hackanet.json.dto.MemberSuggestionDto;
import com.hackanet.json.dto.TeamDto;
import com.hackanet.json.mappers.MemberSuggestionMapper;
import com.hackanet.json.mappers.TeamMapper;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.services.TeamUserHelperService;
import com.hackanet.services.team.TeamService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    private static final String BY_HACKATHON = "/hackathons";
    private static final String BY_HACKATHON_JDBC = BY_HACKATHON + "/jdbc";
    private static final String MEMBERS = "/members";

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamUserHelperService teamUserHelperService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private MemberSuggestionMapper memberSuggestionMapper;

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get teams suggestions (DEPRECATED: it's better to use 'GET SUGGESTIONS BY HACKATHON' method)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamDto>> getSuggestions(@AuthenticationPrincipal User user) {
        List<Team> teams = teamService.getTeamsSuggestion(user);
        return ResponseEntity.ok(teamMapper.map(teams));
    }


    @GetMapping(BY_HACKATHON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get teams suggestions by hackathonId (can be null)", nickname = "GET SUGGESTIONS BY HACKATHON")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamDto>> getSuggestionsByHackathons(@RequestParam(required = false) Long id,
                                                                    @AuthenticationPrincipal User user) {
        List<Team> teams = teamService.getTeamsSuggestion(user, id);
        return ResponseEntity.ok(teamMapper.map(teams));
    }

    @GetMapping(BY_HACKATHON_JDBC)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get teams suggestions by hackathonId (can be null)", nickname = "GET SUGGESTIONS BY HACKATHON")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamDto>> getSuggestionsByHackathonsUsingJDBC(@RequestParam(required = false) Long id,
                                                                             @AuthenticationPrincipal User user) {
        List<Team> teams = teamService.getTeamsSuggestionUsingJDBC(user, id);
        return ResponseEntity.ok(teamMapper.map(teams));
    }

    @GetMapping(MEMBERS)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get members suggestions for teams by hackathonId (can be null)", nickname = "GET SUGGESTIONS BY HACKATHON")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MemberSuggestionDto>> getMembersSuggestions(@RequestParam Long teamId,
                                                                           @RequestParam(required = false) Long hackathonId,
                                                                           @AuthenticationPrincipal User user,
                                                                           HttpServletRequest request) {
        final List<User> suggestions = teamUserHelperService.getMembersSuggestions(user, teamId, hackathonId);
        return ResponseEntity.ok(memberSuggestionMapper.map(suggestions));
    }
}
