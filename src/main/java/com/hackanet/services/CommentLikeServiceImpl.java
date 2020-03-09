package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.Comment;
import com.hackanet.models.CommentLike;
import com.hackanet.models.User;
import com.hackanet.models.enums.LikeType;
import com.hackanet.repositories.CommentLikeRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentService commentService;

    @Override
    public CommentLike create(User user, Long commentId, LikeType type) {
        CommentLike like = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());
        if (like != null) {
            throw new BadRequestException("Like for this comment already exists");
        }
        Comment comment = commentService.get(commentId);
        like = CommentLike.builder()
                .comment(comment)
                .user(user)
                .type(type)
                .build();
        return commentLikeRepository.save(like);
    }

    @Override
    public void delete(User user, Long likeId) {
        CommentLike commentLike = get(likeId);
        SecurityUtils.checkCommentLikeAccess(commentLike, user);
        commentLikeRepository.delete(commentLike);
    }

    @Override
    public CommentLike update(User user, Long commentId, LikeType type) {
        CommentLike like = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());
        like.setType(type);
        return commentLikeRepository.save(like);
    }

    @Override
    public List<CommentLike> getByCommentId(Long commentId) {
        return commentLikeRepository.findByCommentId(commentId);
    }

    private CommentLike get(Long id) {
        return commentLikeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment like with id = " + id + " not found"));
    }
}
