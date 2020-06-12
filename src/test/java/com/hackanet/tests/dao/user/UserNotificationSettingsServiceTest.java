package com.hackanet.tests.dao.user;

import com.hackanet.json.forms.UserNotificationSettingsUpdateForm;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserNotificationSettings;
import com.hackanet.repositories.user.UserNotificationSettingRepository;
import com.hackanet.services.user.UserNotificationSettingsService;
import com.hackanet.tests.dao.AbstractDaoTest;
import com.hackanet.tests.dao.TestEntityCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/12/20
 */
public class UserNotificationSettingsServiceTest extends AbstractDaoTest {

    @Autowired
    private UserNotificationSettingsService userNotificationSettingsService;

    @Autowired
    private UserNotificationSettingRepository userNotificationSettingRepository;

    @Override
    @Before
    public void init() {
        clearUpTables("user_notification_settings", "users", "files_info");
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        prepareDatabase();
    }

    @Override
    protected void prepareDatabase() {
        executeScripts("file_info_data");
        executeScripts("user_data");
    }

    @Test
    public void getOrCreateDefaultsSettingsForUserTest() {
        final User user = TestEntityCreator.getUser(1L);
        final UserNotificationSettings settings = TestEntityCreator.getUserNotificationSettings(1L, user);
        final UserNotificationSettings actual = userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(user);
        assertEquals(settings.getId(), actual.getId());
        final Integer count = jdbcTemplate.queryForObject("select count(*) from user_notification_settings", Integer.class);
        assertEquals(1, count);
    }

    @Test
    public void updateForUserTest() {
        UserNotificationSettingsUpdateForm updateForm = new UserNotificationSettingsUpdateForm();
        updateForm.setEmailEnabled(true);
        updateForm.setPushEnabled(true);
        final long now = 25200000L;
        updateForm.setDontDisturbFrom(now);
        updateForm.setDontDisturbTo(now + 100000);
        final User user = TestEntityCreator.getUser(1L);
        final UserNotificationSettings settings = TestEntityCreator.getUserNotificationSettings(1L, user, updateForm);
        final UserNotificationSettings actual = userNotificationSettingsService.updateForUser(user, updateForm);
        assertEquals(settings.getDontDisturbFrom(), actual.getDontDisturbFrom());
        assertEquals(settings.getDontDisturbTo(), actual.getDontDisturbTo());
        assertEquals(settings.getEmailEnabled(), actual.getEmailEnabled());
        assertEquals(settings.getPushEnabled(), actual.getPushEnabled());
    }
}
