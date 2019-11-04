package com.hackanet.json.mappers;

import com.hackanet.json.dto.*;
import com.hackanet.models.FileInfo;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Post;
import com.hackanet.models.User;
import com.hackanet.services.PostLikeService;
import com.hackanet.services.PostViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
@Component("postMapper")
public class PostMapper implements Mapper<Post, PostDto> {

    @Autowired
    private Mapper<User, UserSimpleDto> userMapper;
    @Autowired
    private Mapper<Hackathon, HackathonDto> hackathonMapper;
    @Autowired
    private Mapper<FileInfo, FileInfoDto> fileMapper;
    @Autowired
    private PostLikeService postLikeService;
    @Autowired
    private PostViewService postViewService;

    @Override
    public PostDto map(Post from) {
        PostDto postDto = PostDto.builder()
                .id(from.getId())
                .title(from.getTitle())
                .content(from.getContent())
                .author(userMapper.map(from.getOwner()))
                .date(from.getDate())
                .likesCount(postLikeService.getCountOfPostLikes(from.getId()))
                .views(postViewService.countOfUniqueViews(from.getId()))
                .build();

        if (from.getHackathon() != null)
            postDto.setHackathonDto(hackathonMapper.map(from.getHackathon()));
        if (from.getImages() != null && !from.getImages().isEmpty()) {
            postDto.setImages(fileMapper.map(from.getImages()));
        }
        return postDto;
    }
}
