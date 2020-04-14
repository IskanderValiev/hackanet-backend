package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.hackathon.Subscription;
import com.hackanet.models.user.User;
import com.hackanet.repositories.SubscriptionRepository;
import com.hackanet.services.hackathon.HackathonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/6/19
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    @Lazy
    private HackathonService hackathonService;

    @Override
    public Subscription subscribe(User user, Long hackathonId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndHackathonId(user.getId(), hackathonId);
        if (subscription != null) {
            return subscription;
        }
        subscription = Subscription.builder()
                .user(user)
                .hackathon(hackathonService.get(hackathonId))
                .build();
        return subscriptionRepository.save(subscription);
    }

    @Override
    public void unsubscribe(User user, Long hackathonId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndHackathonId(user.getId(), hackathonId);
        if (subscription == null)
            throw new NotFoundException("Subscription with userId = " + user.getId() + " and hackathonId = " + hackathonId + " not found");
        subscriptionRepository.delete(subscription);
    }

    @Override
    public List<Subscription> getAllSubscriptionsByUser(User user) {
        return subscriptionRepository.findByUserId(user.getId());
    }

    @Override
    public List<User> getAllSubscribersByHackathonId(Long hackathonId) {
        List<Subscription> subscriptions = subscriptionRepository.findByHackathonId(hackathonId);
        return subscriptions.stream()
                .map(Subscription::getUser)
                .collect(Collectors.toList());
    }
}
