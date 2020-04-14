package com.hackanet.services.comment;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.CommentCreateForm;
import com.hackanet.json.forms.CommentUpdateForm;
import com.hackanet.models.comment.Comment;
import com.hackanet.models.user.User;
import com.hackanet.repositories.comment.CommentRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.post.PostService;
import com.hackanet.services.user.UserService;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Override
    public Comment get(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id = " + id + " not found"));
    }

    @Override
    public Comment create(User user, CommentCreateForm form) {
        // TODO: 3/9/20 check if user is confirmed
        Comment comment = Comment.builder()
                .text(form.getText())
                .post(postService.get(form.getPostId()))
                .user(userService.get(form.getUserId()))
                .date(DateTimeUtil.epochToLocalDateTime(System.currentTimeMillis()))
                .replyParent(get(form.getReplyParentId()))
                .edited(false)
                .build();
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(User user, Long id, CommentUpdateForm form) {
        Comment comment = get(id);
        SecurityUtils.checkCommentAccess(comment, user);
        comment.setText(form.getText());
        comment.setEdited(true);
        return commentRepository.save(comment);
    }

    @Override
    public void delete(User user, Long id) {
        Comment comment = get(id);
        SecurityUtils.checkCommentDeletingAccess(comment, user);
        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> getByPost(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }
}
