package com.hackanet.repositories.skill;

import com.hackanet.models.skill.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findAllByNameLcIgnoreCaseContaining(String name);
    List<Skill> findAllByIdIn(List<Long> ids);
    Optional<Skill> findByNameIgnoreCase(String name);
}
