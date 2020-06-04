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
import java.util.Optional;
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
        return mostRelevantSkillsForSkills(user.getSkills());
    }

    @Override
    public List<Skill> mostRelevantSkills(Team team) {
        List<Skill> skills = new ArrayList<>();
        final List<TeamMember> members = team.getMembers();
        members.forEach(teamMember -> {
            final List<Skill> relevantSkills = mostRelevantSkillsForSkills(teamMember.getSkills());
            skills.addAll(relevantSkills);
        });
        return skills;
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

    private List<Skill> mostRelevantSkillsForSkills(List<Skill> skills) {
        final ArrayList<AuxiliarySkillCombination> list = new ArrayList<>();
        skills.forEach(skill -> {
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
                .distinct()
                .collect(Collectors.toList());
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
                        .map(TeamMember::getSkills)
                        .forEach(teamMemberSkill ->
                                teamMemberSkill.stream()
                                        .filter(s -> !skill.equals(s))
                                        .forEach(ops -> {
                                            createIfNotExistsAndIncreaseCount(skill.getId(), ops.getId());
                                            createIfNotExistsAndIncreaseCount(ops.getId(), skill.getId());
                                        })
                        )
        );
    }

    private void createIfNotExistsAndIncreaseCount(Long skill, Long skillUsedWith) {
        final Optional<SkillCombination> optionalCombination =
                skillCombinationRepository.findBySkillIdAndSkillUsedWith(skill, skillUsedWith);
        final SkillCombination skillCombination = optionalCombination.orElse(SkillCombination.builder()
                .skillId(skill)
                .skillUsedWith(skillUsedWith)
                .countOfCombination(0L)
                .build());
        skillCombination.setCountOfCombination(skillCombination.getCountOfCombination() + 1);
        skillCombinationRepository.save(skillCombination);
    }

    private void decreaseCount(Long skill, Long skillUsedWith) {
        final Optional<SkillCombination> optionalCombination = skillCombinationRepository.findBySkillIdAndSkillUsedWith(skill, skillUsedWith);
        if (!optionalCombination.isPresent()) {
            return;
        }
        final SkillCombination combination = optionalCombination.get();
        combination.setCountOfCombination(combination.getCountOfCombination() == 0 ?
                0 : combination.getCountOfCombination() - 1);
        skillCombinationRepository.save(combination);
    }
}
