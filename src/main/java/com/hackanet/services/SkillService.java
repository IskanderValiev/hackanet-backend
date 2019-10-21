package com.hackanet.services;

import com.hackanet.models.Skill;
import com.hackanet.models.User;

import java.util.List;

public interface SkillService {
    Skill add(String name);
    Skill update(Long id, String name);
    void delete(Long id);
    List<Skill> getAll();
    Skill get(Long id);
    List<Skill> getByNameLike(String name);
}
