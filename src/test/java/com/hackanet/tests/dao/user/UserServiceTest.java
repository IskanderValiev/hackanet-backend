package com.hackanet.tests.dao.user;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.json.forms.UserSearchForm;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserToken;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.repositories.user.UserTokenRepository;
import com.hackanet.security.enums.Role;
import com.hackanet.security.utils.PasswordUtil;
import com.hackanet.services.PortfolioService;
import com.hackanet.services.user.UserNotificationSettingsService;
import com.hackanet.services.user.UserServiceImpl;
import com.hackanet.services.user.UserTokenService;
import com.hackanet.tests.dao.AbstractDaoTest;
import com.hackanet.tests.dao.TestEntityCreator;
import com.hackanet.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/7/20
 */
@ActiveProfiles("test")
@Slf4j
public class UserServiceTest extends AbstractDaoTest {


    @MockBean
    private UserTokenService userTokenService;

    @MockBean
    private UserTokenRepository userTokenRepository;

    @MockBean
    private PasswordEncoder encoder;

    // required to pass the test
    @MockBean
    private UserNotificationSettingsService userNotificationSettingsService;

    @MockBean
    private PortfolioService portfolioService;

    @MockBean
    private PasswordUtil passwordUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserServiceImpl userService;

    private static boolean dbInitialized = false;

    @Override
    @Before
    public void init() {
        if (!dbInitialized) {
            jdbcTemplate = new JdbcTemplate(dataSource);
            executeScripts("file_info_data");
            log.info("database initializing");
        }
        dbInitialized = true;
    }

    @Override
    protected void prepareDatabase() {
        jdbcTemplate.update("delete from users");
        executeScripts("user_data");
    }

    @Test
    public void registerTest() {
        //creating a new object of UserRegistrationForm
        final UserRegistrationForm registrationForm = new UserRegistrationForm();
        registrationForm.setEmail("aaa@gmail.com");
        registrationForm.setPassword("qwerty123");
        //based on the form above, a user is created
        final User user = getUser(registrationForm);
        final TokenDto tokenDto = getToken(user);
        //imitating the proper work of password utils and user token service
        BDDMockito.given(passwordUtil.hash(registrationForm.getPassword())).willReturn(registrationForm.getPassword());
        BDDMockito.given(userTokenService.getTokenByUser(user)).willReturn(tokenDto);
        //checking if the given userId equals the user's id that has been created above
        assertEquals(userService.register(registrationForm).getUserId(), user.getId());
        //querying for the user with specific email
        final List<Long> result = jdbcTemplate.queryForList("select users.id from users where email = '" + registrationForm.getEmail() + "';", Long.class);
        //assert if the user exists
        assertEquals(1, result.size());
        //assert if the user id equals to result's user id
        assertEquals(user.getId(), result.get(0));
        //assert if the method of registration is thrown when inserting user with the email that already exists in database
        org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> userService.register(registrationForm));
    }

    @Test
    public void loginTest() {
        prepareDatabase();
        final UserLoginForm userLoginForm = new UserLoginForm();
        userLoginForm.setEmail("test1@gmail.com");
        userLoginForm.setPassword("test_password1");
        User user = getUser(userLoginForm);
        TokenDto tokenDto = getToken(user);
        UserToken userToken = getUserToken(user);
        BDDMockito.given(passwordUtil.matches(userLoginForm.getPassword(), user.getHashedPassword())).willReturn(true);
        BDDMockito.given(userTokenRepository.findByUserId(user.getId())).willReturn(userToken);
        BDDMockito.given(userTokenService.buildTokenDtoByUser(user, userToken)).willReturn(tokenDto);
        TokenDto actualResult = userService.login(userLoginForm);
        assertEquals(actualResult.getUserId(), tokenDto.getUserId());
    }

    @Test
    public void getByIdsTest() {
        prepareDatabase();
        final List<User> users = LongStream.range(1, 4)
                .mapToObj(TestEntityCreator::getUser)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
        final List<Long> ids = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
        final List<User> actual = userService.getByIds(ids).stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
        Assertions.assertThat(actual.size() == users.size());
        users.forEach(u -> {
            final User user = actual.get(u.getId().intValue() - 1);
            assertEquals(u.getEmail(), user.getEmail());
            assertEquals(u.getName(), user.getName());
            assertEquals(u.getId(), user.getId());
        });
    }

    @Test
    public void userListTest() {
        prepareDatabase();
        UserSearchForm form = new UserSearchForm();
        form.setName("test1");
        final List<User> actual = userService.userList(form);
        assertEquals(actual.size(), 1);
    }

    @After
    public void afterTest() {
        log.info("Tests finished");
    }

    private TokenDto getToken(User user) {
        return TokenDto.builder()
                .userId(user.getId())
                .accessTokenExpiresAt(DateTimeUtil.localDateTimeToLong(LocalDateTime.now().plusHours(4)))
                .refreshTokenExpiresAt(DateTimeUtil.localDateTimeToLong(LocalDateTime.now().plusDays(180)))
                .accessToken("test_access_token")
                .refreshToken("test_refresh_token")
                .build();
    }

    private User getUser(UserRegistrationForm form) {
        final User user = User.builder()
                .role(Role.USER)
                .email(form.getEmail())
                .accessTokenParam("test_token_param")
                .refreshTokenParam("test_refresh_token_param")
                .emailConfirmationCode("test_email_confirmation_code")
                .emailConfirmed(false)
                .hashedPassword(form.getPassword())
                .lastRequestTime(LocalDateTime.now())
                .build();
        user.setId(1L);
        return user;
    }

    private User getUser(UserLoginForm form) {
        User user = User.builder()
                .email(form.getEmail())
                .hashedPassword(form.getPassword())
                .build();
        user.setId(1L);
        return user;
    }

    private UserToken getUserToken(User user) {
        return UserToken.builder()
                .user(user)
                .accessTokenExpiresAt(LocalDateTime.now().plusHours(4))
                .refreshTokenExpiresAt(LocalDateTime.now().plusDays(180))
                .refreshToken("test_refresh_token")
                .build();
    }
}
