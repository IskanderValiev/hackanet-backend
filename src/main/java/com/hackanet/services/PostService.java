package com.hackanet.services;

import com.hackanet.json.forms.PostCreateForm;
import com.hackanet.json.forms.PostSearchForm;
import com.hackanet.json.forms.PostUpdateForm;
import com.hackanet.models.Post;
import com.hackanet.models.User;
import com.hackanet.models.enums.PostImportance;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
public interface PostService extends RetrieveService<Post> {
    Post add(PostCreateForm  form, User user);
    Post update(Long id, User user, PostUpdateForm form);
    List<Post> getByHackathon(Long hackathon);
    List<Post> getByUser(Long user);
    void delete(Long id, User user);
    Post changePostImportance(Long id, PostImportance importance);
    List<Post> getByImportance(PostImportance importance);
    List<Post> postList(PostSearchForm form);
    List<Post> getLikedPostsForUser(Long userId);
}
