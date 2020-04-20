package com.hackanet.services.team;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.exceptions.SkillNumberViolationException;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.user.User;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;
import com.hackanet.repositories.TeamMemberRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.skill.SkillCombinationHelperService;
import com.hackanet.services.skill.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 2/18/20
 */
@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private SkillService skillService;

    @Autowired
    private SkillCombinationHelperService skillCombinationHelperService;

    @Override
    public void addTeamMember(User user, Team team, List<Skill> skills) {
        checkSkillsCount(skills);
        TeamMember teamMember = build(user, team, skills);
        teamMemberRepository.save(teamMember);
    }

    @Override
    public void addTeamMember(User user, Team team) {
        addTeamMember(user, team, null);
    }

    @Override
    public void deleteTeamMember(TeamMember teamMember) {
        teamMemberRepository.delete(teamMember);
    }

    @Override
    public void deleteTeamMember(Long teamId, Long userId) {
        final TeamMember teamMember = getMemberByUserIdAndTeamId(userId, teamId);
        teamMemberRepository.delete(teamMember);
        skillCombinationHelperService.reset(teamMember);
    }

    @Override
    @Transactional
    public TeamMember updateSkills(Long teamId, List<Long> skillsIds, User user) {
        checkSkillsCount(skillsIds);
        TeamMember teamMember = getMemberByUserIdAndTeamId(user.getId(), teamId);
        if (!teamMember.getTeam().getRelevant()) {
            throw new BadRequestException("The team is not relevant");
        }
        if (Boolean.TRUE.equals(teamMember.getSkillsUpdated())) {
            throw new ForbiddenException("Skills have been updated");
        }
        SecurityUtils.checkTeamMemberAccess(teamMember, user);
        skillCombinationHelperService.reset(teamMember);
        teamMember.setSkills(skillService.getByIds(skillsIds));
        skillCombinationHelperService.update(teamMember);
        teamMember.setSkillsUpdated(true);
        return teamMemberRepository.save(teamMember);
    }

    @Override
    public TeamMember get(Long id) {
        return teamMemberRepository.findById(id).orElseThrow(() -> new NotFoundException("Team member with id = " + id + " not found"));
    }

    @Override
    public List<Team> getTeams(Long userId) {
        List<TeamMember> teams = teamMemberRepository.findAllByUserId(userId);
        return teams.stream().map(TeamMember::getTeam).collect(Collectors.toList());
    }

    @Override
    public List<TeamMember> getMembers(Long teamId) {
        return teamMemberRepository.findAllByTeamId(teamId);
    }

    @Override
    public TeamMember getMemberByUserIdAndTeamId(Long userId, Long teamId) {
        return teamMemberRepository.findByUserIdAndTeamId(userId, teamId);
    }

    @Override
    public boolean exists(Long userId, Long teamId) {
        return teamMemberRepository.existsByUserIdAndTeamId(userId, teamId);
    }

    private TeamMember build(User user, Team team, List<Skill> skills) {
        return TeamMember.builder()
                .user(user)
                .team(team)
                .skills(skills)
                .build();
    }

    private void checkSkillsCount(List skills) {
        if (skills != null && skills.size() > 3) {
            throw new SkillNumberViolationException();
        }
    }
}
