package com.hackanet.services.user;

import com.hackanet.json.forms.UserReviewCreateForm;
import com.hackanet.models.ReviewStatistic;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserReview;
import com.hackanet.services.RetrieveService;

import java.util.List;

public interface UserReviewService extends RetrieveService<UserReview> {
    UserReview createReview(User user, UserReviewCreateForm form);
    ReviewStatistic getReviewsCountAndUserRating(Long id);
    List<UserReview> getAllByUser(Long userId);
    void delete(User user, Long reviewId);
}
