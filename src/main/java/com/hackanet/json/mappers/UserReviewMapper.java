package com.hackanet.json.mappers;

import com.hackanet.json.dto.UserReviewDto;
import com.hackanet.models.UserReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/21/19
 */
@Component
public class UserReviewMapper implements Mapper<UserReview, UserReviewDto> {

    @Autowired
    private UserSimpleMapper userSimpleMapper;
    @Autowired
    private TeamMapper teamMapper;

    @Override
    public UserReviewDto map(UserReview from) {
        if (from == null) {
            return null;
        }
        UserReviewDto dto = UserReviewDto.builder()
                .id(from.getId())
                .teamDto(teamMapper.map(from.getTeam()))
                .mark(from.getMark())
                .reviewMessage(from.getReviewMessage())
                .anonymously(Boolean.TRUE.equals(from.isAnonymously()))
                .build();

        if (Boolean.TRUE.equals(from.isAnonymously()))
            dto.setUser(userSimpleMapper.map(from.getUser()));
        return dto;
    }
}
