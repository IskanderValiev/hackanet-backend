package com.hackanet.services;

import com.hackanet.models.user.User;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/25/20
 */
public interface TeamUserHelperService {
    List<User> getMembersSuggestions(User user, Long teamId, Long hackathonId);
}
