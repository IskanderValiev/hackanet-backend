package com.hackanet.tests.dao.user;

import com.hackanet.exceptions.AlreadyExistsException;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.forms.UserReviewCreateForm;
import com.hackanet.models.ReviewStatistic;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserReview;
import com.hackanet.repositories.user.UserReviewRepository;
import com.hackanet.services.hackathon.JoinToHackathonRequestService;
import com.hackanet.services.team.TeamService;
import com.hackanet.services.user.UserReviewServiceImpl;
import com.hackanet.services.user.UserService;
import com.hackanet.tests.dao.AbstractDaoTest;
import com.hackanet.tests.dao.TestEntityCreator;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/10/20
 */
public class UserReviewServiceTest extends AbstractDaoTest {

    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private UserReviewServiceImpl userReviewService;

    @MockBean
    private UserService userService;

    @MockBean
    private TeamService teamService;

    @Autowired
    private EntityManager entityManager;
    @MockBean
    private JoinToHackathonRequestService joinToHackathonRequestService;

    @Override
    @Before
    public void init() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        prepareDatabase();
    }

    @Override
    public void prepareDatabase() {
        clearDatabase(true);
        executeScripts("file_info_data");
        executeScripts("user_data");
        executeScripts("team_data");
        executeScripts("team_member_data");
    }

    @Test
    public void createReviewTest() {
        UserReviewCreateForm form = new UserReviewCreateForm();
        form.setReviewedUserId(2L);
        form.setMark(new Random().nextInt(5));
        form.setTeamId(1L);
        final User user = TestEntityCreator.getUser(1L);
        final User anotherUser = TestEntityCreator.getUser(2L);
        final Hackathon hackathon = TestEntityCreator.getHackathon(1L, user.getId());
        successfulCreateReviewTest(user, anotherUser, hackathon, form);
        exceptionCreateReviewTest(user, anotherUser, hackathon, form);
    }

    @Test
    public void getReviewsCountAndUserRatingTest() {
        final User user1 = TestEntityCreator.getUser(1L);
        final User user2 = TestEntityCreator.getUser(2L);
        final User userToReview = TestEntityCreator.getUser(3L);
        final Hackathon hackathon = TestEntityCreator.getHackathon(1L, user1.getId());
        final Team team = TestEntityCreator.getTeam(1L, 1L, Lists.newArrayList(user1.getId(), user2.getId(), userToReview.getId()), hackathon);
        final List<UserReviewCreateForm> forms = IntStream.range(1, 3)
                .mapToObj(num -> {
                    UserReviewCreateForm userReviewCreateForm = new UserReviewCreateForm();
                    userReviewCreateForm.setTeamId(1L);
                    userReviewCreateForm.setMark(new Random().nextInt(5));
                    userReviewCreateForm.setReviewedUserId(3L);
                    return userReviewCreateForm;
                })
                .collect(Collectors.toList());
        BDDMockito.given(teamService.get(1L)).willReturn(team);
        BDDMockito.given(userService.get(user1.getId())).willReturn(user1);
        BDDMockito.given(userService.get(user2.getId())).willReturn(user2);
        BDDMockito.given(userService.get(userToReview.getId())).willReturn(userToReview);
        BDDMockito.given(joinToHackathonRequestService.isHackathonAttended(hackathon, user1)).willReturn(true);
        BDDMockito.given(joinToHackathonRequestService.isHackathonAttended(hackathon, user2)).willReturn(true);
        BDDMockito.given(joinToHackathonRequestService.isHackathonAttended(hackathon, userToReview)).willReturn(true);
        userReviewService.createReview(user1, forms.get(0));
        userReviewService.createReview(user2, forms.get(1));
        final double avgMark = forms.stream().mapToInt(UserReviewCreateForm::getMark).average().orElse(0.0);
        final ReviewStatistic reviewStatistic = new ReviewStatistic(avgMark, 2L);
        final ReviewStatistic actual = userReviewService.getReviewsCountAndUserRating(3L);
        assertEquals(reviewStatistic.getAverage(), actual.getAverage());
        assertEquals(reviewStatistic.getCount(), actual.getCount());
    }

    @Test
    public void deleteTest() {
        UserReviewCreateForm form = new UserReviewCreateForm();
        form.setReviewedUserId(2L);
        form.setMark(new Random().nextInt(5));
        form.setTeamId(1L);
        final User user1 = TestEntityCreator.getUser(1L);
        final User userToReview = TestEntityCreator.getUser(2L);
        final Hackathon hackathon = TestEntityCreator.getHackathon(1L, user1.getId());
        final Team team = TestEntityCreator.getTeam(1L, 1L, Lists.newArrayList(user1.getId(), userToReview.getId()), hackathon);
        BDDMockito.given(teamService.get(1L)).willReturn(team);
        BDDMockito.given(userService.get(user1.getId())).willReturn(user1);
        BDDMockito.given(userService.get(userToReview.getId())).willReturn(userToReview);
        BDDMockito.given(joinToHackathonRequestService.isHackathonAttended(hackathon, user1)).willReturn(true);
        final UserReview review = userReviewService.createReview(user1, form);
        userReviewService.delete(user1, review.getId());
        final Integer count = jdbcTemplate.queryForObject("select count(*) from user_reviews where reviewed_user_id = " + userToReview.getId() + " and user_id = " + user1.getId(), Integer.class);
        assertEquals(0, count);
    }

    /**
     * Tests the service's method execution in perfect conditionals
     * */
    private void successfulCreateReviewTest(User user, User anotherUser, Hackathon hackathon, UserReviewCreateForm form) {
        // creating a team with parameters passed into this method
        final Team team = TestEntityCreator.getTeam(1L, 1L, Lists.newArrayList(user.getId(), anotherUser.getId()), hackathon);
        final UserReview userReview = TestEntityCreator.getUserReview(1L, anotherUser, team, form.getMark());
        BDDMockito.given(teamService.get(1L)).willReturn(team);
        BDDMockito.given(userService.get(anotherUser.getId())).willReturn(anotherUser);
        BDDMockito.given(joinToHackathonRequestService.isHackathonAttended(hackathon, user)).willReturn(true);
        final UserReview actual = userReviewService.createReview(user, form);
        assertEquals(userReview.getReviewedUser(), actual.getReviewedUser());
        assertEquals(userReview.getTeam().getId(), actual.getTeam().getId());
        assertEquals(userReview.getMark(), actual.getMark());
    }

    /**
     * Tests exceptions thrown by the service's method passing invalid data
     * */
    private void exceptionCreateReviewTest(User user, User anotherUser, Hackathon hackathon, UserReviewCreateForm form) {
        // creating a team with only user to review to cause the createReview method throw a badRequestException
        Team team = TestEntityCreator.getTeam(1L, 1L, Lists.newArrayList(anotherUser.getId()), hackathon);
        final UserReview userReview = TestEntityCreator.getUserReview(1L, anotherUser, team, form.getMark());
        BDDMockito.given(teamService.get(1L)).willReturn(team);
        BDDMockito.given(userService.get(anotherUser.getId())).willReturn(anotherUser);
        final AlreadyExistsException alreadyExistsException = assertThrows(AlreadyExistsException.class, () -> userReviewService.createReview(user, form));
        assertEquals(alreadyExistsException.getMessage(), "Current user has already left a review about this user");
        // if alreadyExistsException has been thrown successfully, database is cleared to test other exceptions to be thrown
        clearDatabase(false);
        final BadRequestException notTeamMates = assertThrows(BadRequestException.class, () -> userReviewService.createReview(user, form));
        assertEquals(notTeamMates.getMessage(), "You have not been team mates");
        // creating a team with 2 members in order to allow the method go down to invokation of isHackathonAttended check
        team = TestEntityCreator.getTeam(1L, 1L, Lists.newArrayList(user.getId(), anotherUser.getId()), hackathon);
        BDDMockito.given(teamService.get(1L)).willReturn(team);
        BDDMockito.given(joinToHackathonRequestService.isHackathonAttended(hackathon, user)).willReturn(false);
        final BadRequestException notAttended = assertThrows(BadRequestException.class, () -> userReviewService.createReview(user, form));
        assertEquals(notAttended.getMessage(), "User has not attended the hackathon");
    }

    private void clearDatabase(boolean completely) {
        this.clearUpTable("user_reviews");
        if (completely) {
            this.clearUpTable("team_members");
            this.clearUpTable("team");
            this.clearUpTable("users");
            this.clearUpTable("files_info");
        }
    }
}
