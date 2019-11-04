package com.hackanet.repositories;

import com.hackanet.models.PostView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    @Query(value = "select count(distinct pw.user_id) + count(case when pw.user_id is null then 1 end) from post_view pw where pw.post_id = :postId", nativeQuery = true)
    Long countOfUniqueViews(@Param("postId") Long postId);
}
