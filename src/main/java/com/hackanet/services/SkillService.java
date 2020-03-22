package com.hackanet.services;

import com.hackanet.models.Skill;

import java.util.List;

public interface SkillService extends RetrieveService<Skill> {
    Skill add(String name);
    Skill update(Long id, String name);
    void delete(Long id);
    List<Skill> getByNameLike(String name);
    List<Skill> getByIds(List<Long> ids);
}
