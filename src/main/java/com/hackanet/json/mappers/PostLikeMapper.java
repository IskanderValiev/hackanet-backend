package com.hackanet.json.mappers;

import com.hackanet.json.dto.PostLikeDto;
import com.hackanet.models.PostLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/2/19
 */
@Component
public class PostLikeMapper implements Mapper<PostLike, PostLikeDto> {

    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Override
    public PostLikeDto map(PostLike from) {
        if (from == null) {
            return null;
        }
        return PostLikeDto.builder()
                .id(from.getId())
                .postId(from.getPost().getId())
                .user(userSimpleMapper.map(from.getUser()))
                .build();
    }
}
