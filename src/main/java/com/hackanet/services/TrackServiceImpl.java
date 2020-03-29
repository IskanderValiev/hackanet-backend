package com.hackanet.services;

import com.google.common.collect.Lists;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.TrackCreateForm;
import com.hackanet.json.forms.TrackUpdateForm;
import com.hackanet.models.Track;
import com.hackanet.models.User;
import com.hackanet.repositories.TrackRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/20/20
 */
@Service
public class TrackServiceImpl implements TrackService {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private HackathonService hackathonService;

    @Override
    public Track create(User user, TrackCreateForm form) {
        Track track = Track.builder()
                .name(form.getName())
                .description(form.getDescription())
                .hackathon(hackathonService.getByAdmin(user.getId()))
                .build();
        return trackRepository.save(track);
    }

    @Override
    public Track update(User user, Long id, TrackUpdateForm form) {
        Track track = get(id);
        SecurityUtils.checkHackathonAccess(track.getHackathon(), user);
        track = Track.builder()
                .name(form.getName())
                .description(form.getDescription())
                .build();
        return trackRepository.save(track);
    }

    @Override
    public void delete(User user, Long id) {
        Track track = get(id);
        SecurityUtils.checkHackathonAccess(track.getHackathon(), user);
        trackRepository.delete(track);
    }

    @Override
    public List<Track> getByHackathonId(Long hackathonId) {
        return trackRepository.findAllByHackathonId(hackathonId);
    }

    @Override
    public List<Track> getMainAndSubTracks(Long mainTrackId, Long subTrackId) {
        Track track = get(mainTrackId);
        Track subTrack = get(subTrackId);
        return Lists.newArrayList(track, subTrack);
    }

    @Override
    public Track get(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Track with id = " + id + " not found"));
    }
}
