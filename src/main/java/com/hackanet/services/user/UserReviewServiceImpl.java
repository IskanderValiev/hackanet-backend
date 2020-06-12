package com.hackanet.services.user;

import com.hackanet.exceptions.AlreadyExistsException;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.UserReviewCreateForm;
import com.hackanet.models.ReviewStatistic;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserReview;
import com.hackanet.repositories.user.UserReviewRepository;
import com.hackanet.services.hackathon.JoinToHackathonRequestService;
import com.hackanet.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.hackanet.security.utils.SecurityUtils.checkUserReviewAccess;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/20/19
 */
@Service
public class UserReviewServiceImpl implements UserReviewService {

    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private JoinToHackathonRequestService requestService;

    @Override
    @Transactional
    public UserReview createReview(User user, UserReviewCreateForm form) {
        boolean reviewExists = userReviewRepository.existsByUserIdAndReviewedUserIdAndTeamId(user.getId(), form.getReviewedUserId(), form.getTeamId());
        if (reviewExists) {
            throw new AlreadyExistsException("Current user has already left a review about this user");
        }
        Team team = teamService.get(form.getTeamId());
        User reviewedUser = userService.get(form.getReviewedUserId());
        List<User> members = team.getMembers().stream().map(TeamMember::getUser).collect(Collectors.toList());
        if (!(members.contains(user) && members.contains(reviewedUser))) {
            throw new BadRequestException("You have not been team mates");
        }
        boolean hackathonAttended = requestService.isHackathonAttended(team.getHackathon(), user);
        if (!hackathonAttended) {
            throw new BadRequestException("User has not attended the hackathon");
        }
        UserReview userReview = UserReview.builder()
                .user(user)
                .team(team)
                .mark(form.getMark())
                .reviewMessage(form.getReviewMessage())
                .reviewedUser(reviewedUser)
                .anonymously(Boolean.TRUE.equals(form.getAnonymously()))
                .build();
        // TODO: 11/21/19 send notification about new review and check that user actually attended hackathon
        return userReviewRepository.save(userReview);
    }

    @Override
    public ReviewStatistic getReviewsCountAndUserRating(Long userId) {
        User user = new User();
        user.setId(userId);
        ReviewStatistic statistic = userReviewRepository.getUserRating(user);
        return statistic;
    }

    @Override
    public List<UserReview> getAllByUser(Long reviewedUserId) {
        return userReviewRepository.findAllByReviewedUserId(reviewedUserId);
    }

    @Override
    @Transactional
    public void delete(User user, Long reviewId) {
        UserReview userReview = get(reviewId);
        checkUserReviewAccess(userReview, user);
        userReviewRepository.delete(userReview);
    }

    @Override
    public UserReview get(Long id) {
        return userReviewRepository.findById(id).orElseThrow(() -> new NotFoundException("User review with id = " + id + " not found"));
    }
}
