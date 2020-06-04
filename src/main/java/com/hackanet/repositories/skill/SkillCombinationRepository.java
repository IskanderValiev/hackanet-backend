package com.hackanet.repositories.skill;

import com.hackanet.models.skill.SkillCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SkillCombinationRepository extends JpaRepository<SkillCombination, String> {
    List<SkillCombination> findBySkillId(Long skillId);
    Optional<SkillCombination> findBySkillIdAndSkillUsedWith(Long skillId, Long skillUsedWith);
}
