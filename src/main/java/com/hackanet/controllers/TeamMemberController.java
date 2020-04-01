package com.hackanet.controllers;

import com.hackanet.json.dto.TeamDto;
import com.hackanet.json.mappers.TeamMapper;
import com.hackanet.models.User;
import com.hackanet.models.team.TeamMember;
import com.hackanet.services.team.TeamMemberService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/7/20
 */
@RestController
@RequestMapping("/team/members")
@Api(tags = "Team Members Controller")
public class TeamMemberController {

    private static final String ONE = "/{id}";

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private TeamMapper teamMapper;

    @PutMapping(ONE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamDto> updateSkills(@PathVariable("id") Long id,
                                                @RequestBody List<Long> skillsIds,
                                                @AuthenticationPrincipal User user) {
        TeamMember teamMember = teamMemberService.updateSkills(id, skillsIds, user);
        return ResponseEntity.ok(teamMapper.map(teamMember.getTeam()));
    }
}
