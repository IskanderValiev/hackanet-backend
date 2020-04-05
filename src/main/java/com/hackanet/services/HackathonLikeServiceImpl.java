package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.User;
import com.hackanet.models.hackathon.HackathonLike;
import com.hackanet.repositories.HackathonLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/5/20
 */
@Service
public class HackathonLikeServiceImpl implements HackathonLikeService {

    @Autowired
    private HackathonLikeRepository hackathonLikeRepository;

    @Autowired
    private HackathonService hackathonService;

    @Override
    public HackathonLike like(User user, Long hackathonId) {
        checkIfExists(user.getId(), hackathonId);

        HackathonLike like = HackathonLike.builder()
                .hackathon(hackathonService.get(hackathonId))
                .user(user)
                .build();
        return hackathonLikeRepository.save(like);
    }

    @Override
    public void unlike(User user, Long hackathonId) {
        HackathonLike like = getByUserIdAndHackathonId(user.getId(), hackathonId);

        hackathonLikeRepository.delete(like);
    }

    @Override
    public Long countByHackathonId(Long hackathonId) {
        return hackathonLikeRepository.countAllByHackathonId(hackathonId);
    }

    private void checkIfExists(Long userId, Long hackathonId) {
        if (hackathonLikeRepository.findByUserIdAndHackathonId(userId, hackathonId).isPresent()) {
            throw new BadRequestException("Hackathon with such userId and hackathonId already exist");
        }
    }

    private HackathonLike getByUserIdAndHackathonId(Long userId, Long hackathonId) {
        return hackathonLikeRepository.findByUserIdAndHackathonId(userId, hackathonId)
                .orElseThrow(() -> new NotFoundException("Hackathon with userId = " + userId +
                                                         " and hackathonId = " + hackathonId + " not found"));
    }
}
