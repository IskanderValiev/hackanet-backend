package com.hackanet.services;

import com.hackanet.json.forms.UserReviewCreateForm;
import com.hackanet.models.ReviewStatistic;
import com.hackanet.models.User;
import com.hackanet.models.UserReview;

import java.util.List;

public interface UserReviewService extends RetrieveService<UserReview> {
    UserReview createReview(User user, UserReviewCreateForm form);
    ReviewStatistic getReviewsCountAndUserRating(Long id);
    List<UserReview> getAllByUser(Long userId);
    void delete(User user, Long reviewId);
}
