package com.hackanet.services;

import com.hackanet.models.Subscription;
import com.hackanet.models.User;

import java.util.List;

public interface SubscriptionService {
    Subscription subscribe(User user, Long hackathonId);
    void unsubscribe(User user, Long hackathonId);
    List<Subscription> getAllSubscriptionsByUser(User user);
    List<User> getAllSubscribersByHackathonId(Long hackathonId);
}
