package com.hackanet.repositories;

import com.hackanet.models.hackathon.HackathonProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/3/19
 */
public interface HackathonProfileViewRepository extends JpaRepository<HackathonProfileView, Long> {
    @Query(value = "select count(distinct hpv.hackathon_id) + count(case when hpv.user_id is null then 1 end) from hackathon_profile_view hpv where hpv.hackathon_id = :hackathonId " +
            "and hpv.timestamp between :from and :to", nativeQuery = true)
    Long countOfUniqueViewsInPeriod(Long hackathonId, Date from, Date to);
}
