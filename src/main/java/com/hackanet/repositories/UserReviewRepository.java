package com.hackanet.repositories;

import com.hackanet.models.ReviewStatistic;
import com.hackanet.models.User;
import com.hackanet.models.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
    boolean existsByUserIdAndReviewedUserIdAndTeamId(Long userId, Long reviewedUserId, Long teamId);
    List<UserReview> findAllByReviewedUserId(Long reviewedUserId);
    @Query(value = "select new com.hackanet.models.ReviewStatistic(avg(ur.mark), count(ur.mark)) from UserReview ur where ur.reviewedUser=:user")
    ReviewStatistic getUserRating(@Param("user") User reviewedUser);
}
