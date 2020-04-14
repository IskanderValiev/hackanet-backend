package com.hackanet.repositories.hackathon;

import com.hackanet.models.hackathon.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findAllByHackathonId(Long hackathonId);
}
