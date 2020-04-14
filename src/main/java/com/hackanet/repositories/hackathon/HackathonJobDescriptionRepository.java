package com.hackanet.repositories.hackathon;

import com.hackanet.models.hackathon.HackathonJobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HackathonJobDescriptionRepository extends JpaRepository<HackathonJobDescription, Long> {
}
