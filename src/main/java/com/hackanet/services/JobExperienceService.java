package com.hackanet.services;

import com.hackanet.json.forms.JobExperienceCreateForm;
import com.hackanet.json.forms.JobExperienceUpdateForm;
import com.hackanet.models.JobExperience;
import com.hackanet.models.user.User;

import java.util.List;

public interface JobExperienceService {
    JobExperience addForPortfolio(User user, JobExperienceCreateForm form);
    void delete(User user, Long id);
    JobExperience get(Long id);
    JobExperience update(Long id, User user, JobExperienceUpdateForm form);
    List<JobExperience> getByIdsIn(List<Long> jobExperience);
}
