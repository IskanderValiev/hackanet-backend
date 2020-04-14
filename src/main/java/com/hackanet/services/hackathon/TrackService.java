package com.hackanet.services.hackathon;

import com.hackanet.json.forms.TrackCreateForm;
import com.hackanet.json.forms.TrackUpdateForm;
import com.hackanet.models.hackathon.Track;
import com.hackanet.models.user.User;
import com.hackanet.services.RetrieveService;

import java.util.List;

public interface TrackService extends RetrieveService<Track> {
    Track create(User user, TrackCreateForm form);
    Track update(User user, Long id, TrackUpdateForm form);
    void delete(User user, Long id);
    List<Track> getByHackathonId(Long hackathonId);
    List<Track> getMainAndSubTracks(Long mainTrackId, Long subTrackId);
}
