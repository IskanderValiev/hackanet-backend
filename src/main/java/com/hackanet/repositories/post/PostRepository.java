package com.hackanet.repositories.post;

import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.post.Post;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.PostImportance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByHackathonOrderByDate(Hackathon hackathon);
    List<Post> findAllByOwnerOrderByDate(User user);
    List<Post> findAllByImportance(PostImportance importance);
}
