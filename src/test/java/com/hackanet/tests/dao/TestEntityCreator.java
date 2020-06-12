package com.hackanet.tests.dao;

import com.hackanet.json.forms.UserNotificationSettingsUpdateForm;
import com.hackanet.models.enums.Currency;
import com.hackanet.models.enums.TeamType;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;
import com.hackanet.models.user.Position;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserNotificationSettings;
import com.hackanet.models.user.UserReview;
import com.hackanet.security.enums.Role;
import org.apache.commons.lang.StringUtils;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/10/20
 */
public class TestEntityCreator {

    public static User getUser(Long id) {
        User user = User.builder()
                .email("test" + id + "@gmail.com")
                .name("test" + id)
                .accessTokenParam("test_access_token_param" + id)
                .refreshTokenParam("test_refresh_token_param" + id)
                .emailConfirmationCode("test_confirmation_code" + id)
                .emailConfirmed(true)
                .hashedPassword("test_password" + id)
                .role(Role.USER)
                .build();
        user.setId(id);
        return user;
    }

    public static Team getTeam(Long id, Long teamLeaderId, List<Long> membersIds) {
        final Team team = Team.builder()
                .lookingForHackers(true)
                .name("test_team_name")
                .relevant(true)
                .teamType(TeamType.CONSTANT)
                .teamLeader(getUser(teamLeaderId))
                .members(membersIds.stream()
                        .map(memberId -> getTeamMember(memberId, memberId))
                        .collect(Collectors.toList()))
                .build();
        team.setId(id);
        return team;
    }

    public static Team getTeam(Long id, Long teamLeaderId, List<Long> membersIds, Hackathon hackathon) {
        final Team team = getTeam(id, teamLeaderId, membersIds);
        team.setHackathon(hackathon);
        return team;
    }

    public static TeamMember getTeamMember(Long id, Long userId) {
        TeamMember teamMember = TeamMember.builder()
                .skillsUpdated(false)
                .user(getUser(userId))
                .build();
        teamMember.setId(id);
        return teamMember;
    }

    public static Hackathon getHackathon(Long id, Long ownerId) {
        Hackathon hackathon = Hackathon.builder()
                .approved(true)
                .city("Berlin")
                .country("Germany")
                .currency(Currency.EUR)
                .description("test_hackathon_description")
                .name("test_hackathon_name")
                .startDate(new Date(System.currentTimeMillis()))
                .endDate(new Date(System.currentTimeMillis() + 1000000))
                .owner(getUser(ownerId))
                .deleted(false)
                .build();
        hackathon.setId(id);
        return hackathon;
    }

    public static UserReview getUserReview(Long id, User reviewedUser, Team team, int mark) {
        UserReview userReview = UserReview.builder()
                .anonymously(false)
                .mark(mark)
                .reviewedUser(reviewedUser)
                .team(team)
                .reviewMessage("test_review_message")
                .build();
        userReview.setId(id);
        return userReview;
    }

    public static Position getPosition(Long id, String name) {
        Position position = Position.builder()
                .name(StringUtils.capitalize(name.toLowerCase()))
                .build();
        position.setId(id);
        return position;
    }

    public static UserNotificationSettings getUserNotificationSettings(Long id, User user) {
        UserNotificationSettings settings = UserNotificationSettings.builder()
                .emailEnabled(true)
                .pushEnabled(true)
                .user(user)
                .build();
        settings.setId(id);
        return settings;
    }

    public static UserNotificationSettings getUserNotificationSettings(Long id, User user, UserNotificationSettingsUpdateForm form) {
        UserNotificationSettings settings = UserNotificationSettings.builder()
                .user(user)
                .emailEnabled(form.getEmailEnabled())
                .pushEnabled(form.getPushEnabled())
                .dontDisturbFrom(LocalTime.ofSecondOfDay(form.getDontDisturbFrom() / 1000))
                .dontDisturbTo(LocalTime.ofSecondOfDay(form.getDontDisturbTo() / 1000))
                .build();
        settings.setId(id);
        return settings;
    }
}
