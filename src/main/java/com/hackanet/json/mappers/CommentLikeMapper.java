package com.hackanet.json.mappers;

import com.hackanet.json.dto.CommentLikeDto;
import com.hackanet.models.CommentLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
@Component
public class CommentLikeMapper implements Mapper<CommentLike, CommentLikeDto> {

    @Autowired
    private UserSimpleMapper userMapper;

    @Override
    public CommentLikeDto map(CommentLike from) {
        return CommentLikeDto.builder()
                .id(from.getId())
                .commentId(from.getComment().getId())
                .user(userMapper.map(from.getUser()))
                .build();
    }
}
