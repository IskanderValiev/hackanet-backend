package com.hackanet.tests.dao.user;

import com.hackanet.application.AppConstants;
import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserToken;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.repositories.user.UserTokenRepository;
import com.hackanet.security.enums.Role;
import com.hackanet.services.user.UserService;
import com.hackanet.services.user.UserTokenService;
import com.hackanet.tests.dao.AbstractDaoTest;
import com.hackanet.tests.dao.TestEntityCreator;
import com.hackanet.utils.DateTimeUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/9/20
 */
public class UserTokenServiceTest extends AbstractDaoTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private UserTokenService userTokenService;

    @Override
    @Before
    public void init() {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    protected void prepareDatabase() {
        jdbcTemplate.update("delete from user_tokens");
        executeScripts("file_info_data");
        executeScripts("user_data");
        executeScripts("user_token_data");
    }

    @Test
    public void getOrCreateTokenIfNotExistsTest() {
        prepareDatabase();
        final User user = TestEntityCreator.getUser(1L);
        //a case where userTokenRepository.findByUserId returns existing userToken
        final UserToken actual = userTokenService.getOrCreateTokenIfNotExists(user);
        assertEquals(user.getId(), actual.getUser().getId());
        //a case where userTokenRepository.findByUserId returns null
        final User userWithoutToken = TestEntityCreator.getUser(3L);
        final UserToken actual1 = userTokenService.getOrCreateTokenIfNotExists(userWithoutToken);
        assertEquals(userWithoutToken.getId(), actual1.getUser().getId());
    }

    @Test
    public void updateAccessTokenTest() {
        prepareDatabase();
        final User user = TestEntityCreator.getUser(2L);
        final UserToken userToken = getUserToken(user);
        BDDMockito.given(userService.get(user.getId())).willReturn(user);
        BDDMockito.given(userRepository.save(user)).willReturn(user);
        // a case when a user's refresh token has expired
        org.junit.jupiter.api.Assertions.assertThrows(ForbiddenException.class, () -> userTokenService.updateAccessToken(user));
        userToken.setRefreshTokenExpiresAt(LocalDateTime.now().minusDays(1));
        // a case when a user's refresh token is valid
        final TokenDto token = getToken(user);
        update(user);
        // the test passes if the difference between expected user token expiration time and actual token expiration time is less than 1 second
        Assert.assertTrue(Math.abs(token.getAccessTokenExpiresAt() - userTokenService.updateAccessToken(user).getAccessTokenExpiresAt()) < 1000);
    }

    private UserToken getUserToken(User user) {
        return UserToken.builder()
                .user(user)
                .accessTokenExpiresAt(LocalDateTime.now().plusHours(AppConstants.ACCESS_TOKEN_EXPIRING_TIME_IN_HOURS))
                .refreshTokenExpiresAt(LocalDateTime.now().plusDays(AppConstants.REFRESH_TOKEN_EXPIRING_TIME_IN_DAYS))
                .refreshToken("test_refresh_token")
                .build();
    }

    private TokenDto getToken(User user) {
        return TokenDto.builder()
                .userId(user.getId())
                .accessTokenExpiresAt(DateTimeUtil.localDateTimeToLong(LocalDateTime.now().plusHours(4)))
                .refreshTokenExpiresAt(DateTimeUtil.localDateTimeToLong(LocalDateTime.now().plusDays(180)))
                .accessToken("test_access_token")
                .refreshToken("test_refresh_token")
                .role(Role.USER.toString())
                .build();
    }

    private void update(User user) {
        final UserToken userToken = userTokenRepository.findByUserId(user.getId());
        userToken.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(AppConstants.REFRESH_TOKEN_EXPIRING_TIME_IN_DAYS));
        userTokenRepository.save(userToken);
    }
}
