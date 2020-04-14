package com.hackanet.json.mappers;

import com.hackanet.json.dto.PostSimpleDto;
import com.hackanet.models.post.Post;
import com.hackanet.models.enums.LikeType;
import com.hackanet.models.enums.PostImportance;
import com.hackanet.services.post.PostLikeService;
import com.hackanet.services.post.PostViewService;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/22/20
 */
@Component
public class PostSimpleMapper implements Mapper<Post, PostSimpleDto> {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Autowired
    private PostViewService postViewService;

    @Autowired
    private PostLikeService postLikeService;

    @Override
    public PostSimpleDto map(Post from) {
        if (from == null) {
            return null;
        }
        return PostSimpleDto.builder()
                .id(from.getId())
                .date(DateTimeUtil.localDateTimeToLong(from.getDate()))
                .important(PostImportance.IMPORTANT.equals(from.getImportance()))
                .title(from.getTitle())
                .picture(fileInfoMapper.map(from.getPicture()))
                .author(userSimpleMapper.map(from.getOwner()))
                .viewCount(postViewService.countOfUniqueViews(from.getId()))
                .likesCount(postLikeService.getCountOfPostLikes(from.getId(), LikeType.LIKE))
                .build();
    }
}
