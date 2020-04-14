package com.hackanet.services.hackathon;

import com.hackanet.models.user.User;
import com.hackanet.models.hackathon.HackathonLike;

public interface HackathonLikeService {
    HackathonLike like(User user, Long hackathonId);
    void unlike(User user, Long hackathonId);
    Long countByHackathonId(Long hackathonId);
}
