package com.hackanet.services.skill;

import com.hackanet.models.skill.Skill;
import com.hackanet.services.RetrieveService;

import java.util.List;

public interface SkillService extends RetrieveService<Skill> {
    Skill add(String name);
    Skill update(Long id, String name);
    void delete(Long id);
    List<Skill> getByNameLike(String name);
    List<Skill> getByIds(List<Long> ids);
}
