package com.hackanet.repositories;

import com.hackanet.models.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> getByUserId(Long userId);
}
