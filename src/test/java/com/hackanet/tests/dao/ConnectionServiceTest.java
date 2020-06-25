package com.hackanet.tests.dao;

import com.hackanet.models.user.User;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.services.ConnectionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/17/20
 */
public class ConnectionServiceTest extends AbstractDaoTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionService connectionService;

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
        clearUpTables("connections", "users", "files_info");
        executeScripts("file_info_data");
        executeScripts("user_data");
    }

    @Test
    public void addConnectionTest() {
        final User user = TestEntityCreator.getUser(1L);
        final User userToAdd = TestEntityCreator.getUser(2L);
        connectionService.addConnection(user, userToAdd);
        assertTrue(user.getConnections().size() > 0);
        assertEquals(user.getConnections().size(), userToAdd.getConnections().size());
    }

    @Test
    public void deleteConnectionTest() {
        executeScripts("connection_invitation_data");
        executeScripts("connection_data");
        final User user = jdbcTemplate.query("select u.* from users u where u.id=1", new BeanPropertyRowMapper<>(User.class)).get(0);
        final User userToDelete = jdbcTemplate.query("select u.* from users u where u.id=2", new BeanPropertyRowMapper<>(User.class)).get(0);
        final Integer userConnectionCount = jdbcTemplate.queryForObject("select count(*) from connections where user_id = " + user.getId(), Integer.class);
        final Integer userToDeleteConnectionCount = jdbcTemplate.queryForObject("select count(*) from connections where user_id = " + userToDelete.getId(), Integer.class);
        assertEquals(userConnectionCount, userToDeleteConnectionCount);
        connectionService.deleteConnection(user, userToDelete);
        assertTrue(user.getConnections().size() == 0);
    }

    @Test
    public void getConnectionsSuggestionsTest() {
        executeScripts("connection_invitation_data");
        executeScripts("connection_data");
        final User user = TestEntityCreator.getUser(2L);
        final Set<User> connectionsSuggestions = connectionService.getConnectionsSuggestions(user);
        final User suggestedUser = TestEntityCreator.getUser(3L);
        assertTrue(connectionsSuggestions.contains(suggestedUser));
    }
}
