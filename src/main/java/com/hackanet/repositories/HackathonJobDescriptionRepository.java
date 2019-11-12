package com.hackanet.repositories;

import com.hackanet.models.HackathonJobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HackathonJobDescriptionRepository extends JpaRepository<HackathonJobDescription, Long> {
}
