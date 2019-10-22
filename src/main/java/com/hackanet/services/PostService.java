package com.hackanet.services;

import com.hackanet.json.forms.PostCreateForm;
import com.hackanet.json.forms.PostUpdateForm;
import com.hackanet.models.Post;
import com.hackanet.models.User;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
public interface PostService {
    Post add(PostCreateForm  form, User user);
    Post update(Long id, User user, PostUpdateForm form);
    List<Post> getByHackathon(Long hackathon);
    List<Post> getByUser(Long user);
    Post get(Long id);
    void delete(Long id, User user);
}
