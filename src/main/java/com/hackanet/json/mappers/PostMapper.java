package com.hackanet.json.mappers;

import com.hackanet.json.dto.PostDto;
import com.hackanet.models.Post;
import com.hackanet.models.enums.LikeType;
import com.hackanet.services.CommentService;
import com.hackanet.services.PostLikeService;
import com.hackanet.services.PostViewService;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
@Component("postMapper")
public class PostMapper implements Mapper<Post, PostDto> {

    @Autowired
    private UserSimpleMapper userMapper;

    @Autowired
    private HackathonSimpleMapper hackathonMapper;

    @Autowired
    private FileInfoMapper fileMapper;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostViewService postViewService;

    @Autowired
    private CommentService commentService;

    @Override
    public PostDto map(Post from) {
        if (from == null) {
            return null;
        }
        return PostDto.builder()
                .id(from.getId())
                .title(from.getTitle())
                .content(from.getContent())
                .author(userMapper.map(from.getOwner()))
                .date(DateTimeUtil.localDateTimeToLong(from.getDate()))
                .likesCount(postLikeService.getCountOfPostLikes(from.getId(), LikeType.LIKE))
                .dislikesCount(postLikeService.getCountOfPostLikes(from.getId(), LikeType.DISLIKE))
                .views(postViewService.countOfUniqueViews(from.getId()))
                .picture(fileMapper.map(from.getPicture()))
                .commentsCount(commentService.getByPost(from.getId()).size())
                .hackathon(hackathonMapper.map(from.getHackathon()))
                .build();
    }
}
