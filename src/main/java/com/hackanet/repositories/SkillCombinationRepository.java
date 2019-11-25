package com.hackanet.repositories;

import com.hackanet.models.SkillCombination;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SkillCombinationRepository extends MongoRepository<SkillCombination, String> {
    List<SkillCombination> findBySkillId(Long skillId);
    SkillCombination findBySkillIdAndSkillUsedWith(Long skillId, Long skillUsedWith);
}
