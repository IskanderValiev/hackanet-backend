package com.hackanet.services;

import com.hackanet.json.forms.TrackCreateForm;
import com.hackanet.json.forms.TrackUpdateForm;
import com.hackanet.models.Track;
import com.hackanet.models.User;

import java.util.List;

public interface TrackService extends RetrieveService<Track> {
    Track create(User user, TrackCreateForm form);
    Track update(User user, Long id, TrackUpdateForm form);
    void delete(User user, Long id);
    List<Track> getByHackathonId(Long hackathonId);
    List<Track> getMainAndSubTracks(Long mainTrackId, Long subTrackId);
}
