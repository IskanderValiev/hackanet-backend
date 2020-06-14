package com.hackanet.tests.dao.user;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.forms.UserPhoneTokenAddForm;
import com.hackanet.models.user.UserPhoneToken;
import com.hackanet.push.enums.ClientType;
import com.hackanet.repositories.user.UserPhoneTokenRepository;
import com.hackanet.services.user.UserPhoneTokenService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/14/20
 */
public class UserPhoneTokenServiceTest extends AbstractDaoTest {

    @Autowired
    private UserPhoneTokenRepository userPhoneTokenRepository;

    @Autowired
    private UserPhoneTokenService userPhoneTokenService;

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
        clearUpTables("user_phone_token", "users", "files_info");
        executeScripts("file_info_data");
        executeScripts("user_data");
    }

    @Test
    public void addTest() {
        UserPhoneTokenAddForm form = new UserPhoneTokenAddForm();
        form.setClientType(ClientType.ANDROID);
        form.setDeviceId("device_id_test");
        form.setToken("device_token_test");
        form.setUserId(1L);
        final UserPhoneToken userPhoneToken = TestEntityCreator.getUserPhoneToken(1L, form);
        BDDMockito.given(userService.get(form.getUserId())).willReturn(TestEntityCreator.getUser(1L));
        final UserPhoneToken actual = userPhoneTokenService.add(form);
        assertEquals(userPhoneToken, actual);
        assertThrows(BadRequestException.class, () -> userPhoneTokenService.add(form));
    }
}
