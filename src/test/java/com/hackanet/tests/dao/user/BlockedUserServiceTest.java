package com.hackanet.tests.dao.user;

import com.hackanet.models.user.User;
import com.hackanet.repositories.BlockedUserRepository;
import com.hackanet.services.BlockedUserService;
import com.hackanet.tests.dao.AbstractDaoTest;
import com.hackanet.tests.dao.TestEntityCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/14/20
 */
public class BlockedUserServiceTest extends AbstractDaoTest {

    @Autowired
    private BlockedUserRepository blockedUserRepository;

    @Autowired
    private BlockedUserService blockedUserService;

    @Override
    @Before
    public void init() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        prepareDatabase();
    }

    @Override
    protected void prepareDatabase() {
        executeScripts("file_info_data");
        executeScripts("user_data");
        executeScripts("blocked_user_data");
    }

    @Test
    public void isBlockedTest() {
        final User user = TestEntityCreator.getUser(1L);
        assertTrue(blockedUserService.isBlocked(user));
    }
}
