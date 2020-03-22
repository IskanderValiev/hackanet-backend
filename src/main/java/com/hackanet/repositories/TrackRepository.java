package com.hackanet.repositories;

import com.hackanet.models.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findAllByHackathonId(Long hackathonId);
}
