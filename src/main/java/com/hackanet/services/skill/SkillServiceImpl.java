package com.hackanet.services.skill;

import com.google.common.collect.Lists;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.skill.Skill;
import com.hackanet.repositories.skill.SkillRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Override
    public Skill add(String name) {
        if (!StringUtils.isBlank(name)) {
            Skill skill = Skill.builder()
                    .name(name.trim())
                    .nameLc(name.trim().toLowerCase())
                    .build();

            skill = skillRepository.save(skill);
            return skill;
        } else {
            throw new BadRequestException("Name is empty or null");
        }
    }

    @Override
    public Skill update(Long id, String name) {
        Skill skill = get(id);
        skill.setName(name.trim());
        skill.setNameLc(name.trim().toLowerCase());
        skill = skillRepository.save(skill);
        return skill;
    }

    @Override
    public void delete(Long id) {
        skillRepository.deleteById(id);
    }

    @Override
    public List<Skill> getAll() {
        return skillRepository.findAll();
    }

    @Override
    public Skill get(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> new NotFoundException("Skill with id=" + id + " not found"));
    }

    @Override
    public List<Skill> getByNameLike(String name) {
        return skillRepository.findAllByNameLcIgnoreCaseContaining(name.trim().toLowerCase());
    }

    @Override
    public List<Skill> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Lists.newArrayList();
        }
        return skillRepository.findAllByIdIn(ids);
    }
}
