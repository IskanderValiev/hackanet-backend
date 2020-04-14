package com.hackanet.services.comment;

import com.hackanet.json.forms.CommentCreateForm;
import com.hackanet.json.forms.CommentUpdateForm;
import com.hackanet.models.comment.Comment;
import com.hackanet.models.user.User;
import com.hackanet.services.RetrieveService;

import java.util.List;

public interface CommentService extends RetrieveService<Comment> {
    Comment create(User user, CommentCreateForm form);
    Comment update(User user, Long id, CommentUpdateForm form);
    void delete(User user, Long id);
    List<Comment> getByPost(Long postId);
}
