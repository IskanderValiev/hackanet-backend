package com.hackanet.json.mappers;

import com.hackanet.json.dto.UserDto;
import com.hackanet.models.*;
import com.hackanet.models.user.User;
import com.hackanet.services.user.UserReviewService;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Component
public class UserMapper implements Mapper<User, UserDto> {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private UserReviewService userReviewService;

    @Autowired
    private PositionMapper positionMapper;

    @Override
    public UserDto map(User from) {
        if (from == null) {
            return null;
        }
        ReviewStatistic rating = userReviewService.getReviewsCountAndUserRating(from.getId());
        return UserDto.builder()
                .id(from.getId())
                .email(from.getEmail())
                .name(from.getName())
                .lastname(from.getLastname())
                .country(from.getCountry())
                .city(from.getCity())
                .nickname(from.getNickname())
                .about(from.getAbout())
                .university(from.getUniversity())
                .reviewCount(rating.getCount())
                .rating(rating.getAverage())
                .lastRequestTime(DateTimeUtil.localDateTimeToLong(from.getLastRequestTime()))
                .position(positionMapper.map(from.getPosition()))
                .picture(fileInfoMapper.map(from.getPicture()))
                .skills(skillMapper.map(from.getSkills()))
                .role(from.getRole())
                .build();
    }
}
