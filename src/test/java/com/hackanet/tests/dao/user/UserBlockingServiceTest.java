package com.hackanet.tests.dao.user;

import com.hackanet.models.BlockedUser;
import com.hackanet.models.enums.BlockInitiator;
import com.hackanet.models.enums.BlockReason;
import com.hackanet.models.user.User;
import com.hackanet.repositories.BlockedUserRepository;
import com.hackanet.services.UserBlockingService;
import com.hackanet.services.user.UserService;
import com.hackanet.tests.dao.AbstractDaoTest;
import com.hackanet.tests.dao.TestEntityCreator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/14/20
 */
public class UserBlockingServiceTest extends AbstractDaoTest {

    @Autowired
    private BlockedUserRepository blockedUserRepository;

    @Autowired
    private UserBlockingService userBlockingService;

    @MockBean
    private UserService userService;

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
        clearUpTables("blocked_users", "users", "files_info");
        executeScripts("file_info_data");
        executeScripts("user_data");
    }

    @Test
    public void blockTest() {
        final BlockedUser blockedUser = TestEntityCreator.getBlockedUser(1L, 1L, BlockInitiator.SYSTEM, BlockReason.OTHERS);
        final User user = TestEntityCreator.getUser(1L);
        BDDMockito.given(userService.get(user.getId())).willReturn(user);
        final BlockedUser actual = userBlockingService.block(user.getId(), BlockInitiator.SYSTEM, BlockReason.OTHERS);
        //can't use equals() method because values of the time property of these two BlockedUser entities differ from each other
        assertEquals(blockedUser.getId(), actual.getId());
    }

    @Test
    public void unblockTest() {
        final User user = TestEntityCreator.getUser(1L);
        BDDMockito.given(userService.get(user.getId())).willReturn(user);
        final BlockedUser block = userBlockingService.block(1L, BlockInitiator.SYSTEM, BlockReason.OTHERS);
        userBlockingService.unblock(block.getUser().getId());
        final Boolean unblocked = jdbcTemplate.queryForObject("SELECT bu.canceled from blocked_users bu where user_id = " + block.getUser().getId(), Boolean.class);
        assertTrue(unblocked);
    }
}
