package com.hackanet.json.mappers;

import com.hackanet.json.dto.CommentDto;
import com.hackanet.models.Comment;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
@Component
public class CommentMapper implements Mapper<Comment, CommentDto> {

    @Autowired
    private UserSimpleMapper userMapper;

    @Autowired
    private CommentLikeMapper likeMapper;

    @Override
    public CommentDto map(Comment from) {
        if (from == null) {
            return null;
        }
        return CommentDto.builder()
                .id(from.getId())
                .postId(from.getPost().getId())
                .user(userMapper.map(from.getUser()))
                .date(DateTimeUtil.localDateTimeToLong(from.getDate()))
                .likes(likeMapper.map(from.getLikes()))
                .replies(map(from.getReplies()))
                .edited(from.getEdited())
                .build();
    }
}
