package com.hackanet.repositories.skill;

import com.hackanet.models.skill.SkillCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SkillCombinationRepository extends JpaRepository<SkillCombination, Long> {
    List<SkillCombination> findBySkillId(Long skillId);
    SkillCombination findBySkillIdAndSkillUsedWith(Long skillId, Long skillUsedWith);
}
