package com.hackanet.repositories.skill;

import com.hackanet.models.skill.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findAllByNameLcIgnoreCaseContaining(String name);
    List<Skill> findAllByIdIn(List<Long> ids);
}
