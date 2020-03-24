package com.hackanet.repositories;

import com.hackanet.models.hackathon.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    List<Sponsor> findAllByHackathonId(Long hackathonId);
}
