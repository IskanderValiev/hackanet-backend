package com.hackanet.repositories;

import com.hackanet.models.JobExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobExperienceRepository extends JpaRepository<JobExperience, Long> {
    List<JobExperience> findAllByIdIn(List<Long> ids);
}
