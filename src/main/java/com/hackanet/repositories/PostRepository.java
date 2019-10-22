package com.hackanet.repositories;

import com.hackanet.models.Hackathon;
import com.hackanet.models.Post;
import com.hackanet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByHackathonOrderByDate(Hackathon hackathon);
    List<Post> findAllByOwnerOrderByDate(User user);
}
