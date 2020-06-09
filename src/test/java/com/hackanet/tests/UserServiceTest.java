package com.hackanet.tests;

import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.json.forms.UserSearchForm;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserToken;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.repositories.user.UserTokenRepository;
import com.hackanet.security.enums.Role;
import com.hackanet.services.PortfolioService;
import com.hackanet.services.user.UserNotificationSettingsService;
import com.hackanet.services.user.UserServiceImpl;
import com.hackanet.services.user.UserTokenService;
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

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/7/20
 */
@ActiveProfiles("test")
@Slf4j
public class UserServiceTest extends AbstractDaoTest {

    @MockBean
    private UserRepository userRepository;

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

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserServiceImpl userService;

    private JdbcTemplate jdbcTemplate;

    private static boolean dbInitialized = false;

    @Before
    public void init() {
        if (!dbInitialized) {
            jdbcTemplate = new JdbcTemplate(dataSource);
            executeScripts(applicationContext.getResource("classpath:/file_info_data.sql"));
            log.info("database initializing");
        }
        dbInitialized = true;
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
        //imitating the proper work of user repository
        BDDMockito.given(userRepository.save(user)).willReturn(user);
        BDDMockito.given(userTokenService.getTokenByUser(user)).willReturn(tokenDto);
        //checking if the given userId equals the user's id that has been created above
        Assertions.assertThat(userService.register(registrationForm).getUserId().equals(user.getId()));
    }

    @Test
    public void loginTest() {
        executeScripts(applicationContext.getResource("classpath:/user_data.sql"));
        final UserLoginForm userLoginForm = new UserLoginForm();
        userLoginForm.setEmail("test1@gmail.com");
        userLoginForm.setPassword("qwerty123");
        User user = getUser(userLoginForm);
        TokenDto tokenDto = getToken(user);
        UserToken userToken = getUserToken(user);
        BDDMockito.given(userRepository.findByEmail(userLoginForm.getEmail())).willReturn(Optional.of(user));
        BDDMockito.given(encoder.matches(userLoginForm.getPassword(), user.getHashedPassword())).willReturn(true);
        BDDMockito.given(userTokenRepository.findByUserId(user.getId())).willReturn(userToken);
        BDDMockito.given(userTokenService.buildTokenDtoByUser(user, userToken)).willReturn(tokenDto);
        TokenDto actualResult = userService.login(userLoginForm);
        Assertions.assertThat(actualResult.getUserId().equals(tokenDto.getUserId()));
    }

    @Test
    public void getByIdsTest() {
        final Set<User> users = LongStream.range(1, 4)
                .mapToObj(id -> {
                    final User user = User.builder().name("test" + id).email("email" + id + "@gmail.com").build();
                    user.setId(id);
                    return user;
                })
                .collect(Collectors.toSet());
        final List<Long> ids = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
        BDDMockito.given(userRepository.findAllByIdIn(ids)).willReturn(users);
        final Set<User> actual = userService.getByIds(ids);
        Assertions.assertThat(actual.size() == users.size());
        List<User> usersList = new ArrayList<>(users);
        final List<User> orderedActual = actual.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
        LongStream.range(1, 4)
                .forEach(id -> {
                    final User user = usersList.get((int) (id - 1));
                    final User actualUser = orderedActual.get((int) (id - 1));
                    Assertions.assertThat(user.getEmail().equals(actualUser.getEmail()));
                    Assertions.assertThat(user.getName().equals(actualUser.getName()));
                    Assertions.assertThat(user.getId().equals(actualUser.getId()));
                });
    }

    @Test
    public void userListTest() {
        UserSearchForm form = new UserSearchForm();
        form.setEmail("test1@gmail.com");
        form.setName("test1");
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = ReflectionTestUtils.invokeMethod(userService, "getUsersListQuery", criteriaBuilder, form);
        final TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
        Assertions.assertThat(query.getResultList().size() == 1);
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
