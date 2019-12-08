package com.hackanet.json.mappers;

import com.hackanet.json.dto.SubscriptionDto;
import com.hackanet.models.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/7/19
 */
@Component
public class SubscriptionMapper implements Mapper<Subscription, SubscriptionDto> {

    @Autowired
    private HackathonMapper hackathonMapper;

    @Override
    public SubscriptionDto map(Subscription from) {
        return SubscriptionDto.builder()
                .id(from.getId())
                .hackathonDto(hackathonMapper.map(from.getHackathon()))
                .userId(from.getUser().getId())
                .build();
    }
}
