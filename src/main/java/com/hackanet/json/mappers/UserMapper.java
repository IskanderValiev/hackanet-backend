package com.hackanet.json.mappers;

import com.hackanet.json.dto.FileInfoDto;
import com.hackanet.json.dto.SkillDto;
import com.hackanet.json.dto.UserDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.ReviewStatistic;
import com.hackanet.models.Skill;
import com.hackanet.models.User;
import com.hackanet.services.UserReviewService;
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
    private Mapper<FileInfo, FileInfoDto> mapper;
    @Autowired
    private Mapper<Skill, SkillDto> skillMapper;
    @Autowired
    private UserReviewService userReviewService;

    @Override
    public UserDto map(User from) {
        if (from == null) {
            return null;
        }
        ReviewStatistic rating = userReviewService.getReviewsCountAndUserRating(from.getId());
        UserDto user = UserDto.builder()
                .id(from.getId())
                .email(from.getEmail())
                .phone(from.getPhone())
                .name(from.getName())
                .lastname(from.getLastname())
                .country(from.getCountry())
                .city(from.getCity())
                .about(from.getAbout())
                .reviewCount(rating.getCount())
                .rating(rating.getAverage())
                .lastRequestTime(DateTimeUtil.localDateTimeToLong(from.getLastRequestTime()))
                .build();
        if (from.getPicture() != null)
            user.setImage(mapper.map(from.getPicture()));
        if (from.getSkills() != null)
            user.setSkills(skillMapper.map(from.getSkills()));
        return user;
    }
}
