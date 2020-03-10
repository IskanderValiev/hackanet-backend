package com.hackanet.services;

import com.hackanet.json.forms.CommentCreateForm;
import com.hackanet.json.forms.CommentUpdateForm;
import com.hackanet.models.Comment;
import com.hackanet.models.User;

import java.util.List;

public interface CommentService {
    Comment get(Long id);
    Comment create(User user, CommentCreateForm form);
    Comment update(User user, Long id, CommentUpdateForm form);
    void delete(User user, Long id);
    List<Comment> getByPost(Long postId);
}
