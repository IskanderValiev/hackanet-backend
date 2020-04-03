package com.hackanet.services;

import com.hackanet.models.*;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;
import com.hackanet.repositories.SkillCombinationRepository;
import com.hackanet.services.team.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/24/19
 */
@Service
public class SkillCombinationServiceImpl implements SkillCombinationService {

    @Autowired
    private SkillCombinationRepository skillCombinationRepository;

    @Autowired
    private SkillService skillService;

    @Autowired
    private TeamMemberService teamMemberService;

    @Override
    public void createByTeam(Team team) {
        List<TeamMember> members = teamMemberService.getMembers(team.getId());
        for (TeamMember m : members) {
            combineSkills(m.getUser(), team, members);
        }
    }

    @Override
    public void updateIfUserJoinedToTeam(User user, Team team) {
        List<TeamMember> members = teamMemberService.getMembers(team.getId());
        combineSkills(user, team, members);
    }

    @Override
    public List<Skill> mostRelevantSkills(User user) {
        List<AuxiliarySkillCombination> list = new ArrayList<>();
        user.getSkills().forEach(skill -> {
            List<SkillCombination> combinations = skillCombinationRepository.findBySkillId(skill.getId());
            long combinationCount = combinations.stream()
                    .mapToLong(SkillCombination::getCountOfCombination)
                    .sum();

            for (SkillCombination combination : combinations) {
                double probability = (double) combination.getCountOfCombination() / combinationCount;
                list.add(AuxiliarySkillCombination.builder()
                        .probability(probability)
                        .skillId(skill.getId())
                        .skillUsedWithId(combination.getSkillUsedWith())
                        .build());
            }
        });

        return list.stream()
                .sorted(comparing(AuxiliarySkillCombination::getProbability).reversed())
                .map(AuxiliarySkillCombination::getSkillUsedWithId)
                .map(id -> skillService.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public void recalculate(Team team, TeamMember teamMember) {
        List<TeamMember> members = teamMemberService.getMembers(team.getId());
        members.remove(teamMember);

        members.forEach(member ->
                member.getSkills().forEach(skill ->
                        teamMember.getSkills().forEach(skill1 -> {
                            decreaseCount(skill.getId(), skill1.getId());
                            decreaseCount(skill1.getId(), skill.getId());
                        })
                )
        );
        combineSkills(teamMember.getUser(), team, members);
    }

    /**
     * combines skills which are used by members in team
     *
     * @param user    - user whose skills to combine
     * @param members - team members to combine skills with
     */
    private void combineSkills(User user, Team team, List<TeamMember> members) {
        TeamMember member = teamMemberService.getMemberByUserIdAndTeamId(user.getId(), team.getId());
        members.remove(member);

        member.getSkills().forEach(skill ->
                members.forEach(m ->
                        m.getSkills().forEach(ops -> {
                            if (!skill.equals(ops)) {
                                createIfNotExistsAndIncreaseCount(skill.getId(), ops.getId());
                                createIfNotExistsAndIncreaseCount(ops.getId(), skill.getId());
                            }
                        })
                )
        );
    }

    private void createIfNotExistsAndIncreaseCount(Long skill, Long skillUsedWith) {
        SkillCombination combination = skillCombinationRepository.findBySkillIdAndSkillUsedWith(skill, skillUsedWith);
        if (combination == null) {
            combination = SkillCombination.builder()
                    .skillId(skill)
                    .skillUsedWith(skillUsedWith)
                    .countOfCombination(1L)
                    .build();
        } else {
            combination.setCountOfCombination(combination.getCountOfCombination() + 1);
        }
        skillCombinationRepository.save(combination);
    }

    private void decreaseCount(Long skill, Long skillUsedWith) {
        SkillCombination combination = skillCombinationRepository.findBySkillIdAndSkillUsedWith(skill, skillUsedWith);
        if (combination == null) {
            return;
        }
        combination.setCountOfCombination(combination.getCountOfCombination() - 1);
        skillCombinationRepository.save(combination);
    }
}
