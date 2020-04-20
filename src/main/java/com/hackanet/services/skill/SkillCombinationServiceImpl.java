package com.hackanet.services.skill;

import com.hackanet.models.skill.AuxiliarySkillCombination;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.skill.SkillCombination;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;
import com.hackanet.models.user.User;
import com.hackanet.repositories.skill.SkillCombinationRepository;
import com.hackanet.services.team.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
            combineSkills(m.getUser(), team);
        }
    }

    @Override
    public void updateIfUserJoinedToTeam(User user, Team team) {
        combineSkills(user, team);
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
    public void recalculate(TeamMember teamMember, boolean userDeleted) {
        final Team team = teamMember.getTeam();
        reset(teamMember);
        if (!userDeleted) {
            combineSkills(teamMember.getUser(), team);
        }
    }

    @Override
    public void reset(TeamMember teamMember) {
        final Team team = teamMember.getTeam();
        team.getMembers()
                .stream()
                .filter(m -> !m.equals(teamMember))
                .forEach(member ->
                        member.getSkills().forEach(skill ->
                                teamMember.getSkills().forEach(skill1 -> {
                                    decreaseCount(skill.getId(), skill1.getId());
                                    decreaseCount(skill1.getId(), skill.getId());
                                })
                        )
                );
    }

    /**
     * combines skills which are used by members in team
     *
     * @param user - user whose skills to combine
     */
    private void combineSkills(User user, Team team) {
        TeamMember member = teamMemberService.getMemberByUserIdAndTeamId(user.getId(), team.getId());
        member.getSkills().forEach(skill ->
                team.getMembers().stream()
                        .filter(m -> !m.equals(member))
                        .forEach(m ->
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
