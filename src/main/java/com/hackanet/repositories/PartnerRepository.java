package com.hackanet.repositories;

import com.hackanet.models.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Set<Partner> findByIdIn(Set<Long> ids);
}
