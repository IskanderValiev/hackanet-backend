package com.hackanet.services;

import com.hackanet.models.*;
import com.hackanet.repositories.SkillCombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public void createByTeam(Team team) {
        List<User> participants = team.getParticipants();
        for (User p : participants) {
            combineSkills(p, participants);
        }
    }

    @Override
    public void updateIfUserJoinedToTeam(User user, Team team) {
        List<User> participants = team.getParticipants();
        combineSkills(user, participants);
    }

    @Override
    public List<Skill> mostRelevantSkills(User user) {
        List<AuxiliarySkillCombination> list = new ArrayList<>();
        user.getSkills().forEach(skill -> {
            List<SkillCombination> combinations = skillCombinationRepository.findBySkillId(skill.getId());
            long combinationCount = 0;
            for (SkillCombination combination : combinations) {
                combinationCount += combination.getCountOfCombination();
            }
            for (SkillCombination combination : combinations) {
                double probability = (double) combination.getCountOfCombination() / combinationCount;
                list.add(AuxiliarySkillCombination.builder()
                        .probability(probability)
                        .skillId(skill.getId())
                        .skillUsedWithId(combination.getSkillUsedWith())
                        .build());
            }
        });

        List<Long> sortedList = list.stream()
                .sorted(Comparator.comparing(AuxiliarySkillCombination::getProbability).reversed())
                .map(AuxiliarySkillCombination::getSkillUsedWithId)
                .collect(Collectors.toList());

        List<Skill> orderedByProbability = new ArrayList<>();
        sortedList.forEach(id -> orderedByProbability.add(skillService.get(id)));
        return orderedByProbability;
//        return new ArrayList<>(skillService.getByIds(sortedList));
    }


    /**
     * combines skills which are used by participants in team
     *
     * @param user         - user whose skills to combine
     * @param participants - team participants to combine skills with
     */
    private void combineSkills(User user, List<User> participants) {
        Set<User> otherParticipants = participants.stream()
                .filter(participant -> !participant.equals(user))
                .collect(Collectors.toSet());

        user.getSkills().forEach(skill ->
                otherParticipants.forEach(op ->
                        op.getSkills().forEach(ops -> {
                            if (!skill.equals(ops))
                                createIfNotExistsAndIncreaseCount(skill.getId(), ops.getId());
                        })
                )
        );
    }

    private SkillCombination createIfNotExistsAndIncreaseCount(Long skill, Long skillUsedWith) {
        SkillCombination combination = skillCombinationRepository.findBySkillIdAndSkillUsedWith(skill, skillUsedWith);
        if (combination == null)
            combination = SkillCombination.builder()
                    .skillId(skill)
                    .skillUsedWith(skillUsedWith)
                    .countOfCombination(1L)
                    .build();
        else
            combination.setCountOfCombination(combination.getCountOfCombination() + 1);
        return skillCombinationRepository.save(combination);
    }
}
