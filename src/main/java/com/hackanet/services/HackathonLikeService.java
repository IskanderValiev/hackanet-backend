package com.hackanet.services;

import com.hackanet.models.User;
import com.hackanet.models.hackathon.HackathonLike;

public interface HackathonLikeService {
    HackathonLike like(User user, Long hackathonId);
    void unlike(User user, Long hackathonId);
    Long countByHackathonId(Long hackathonId);
}
