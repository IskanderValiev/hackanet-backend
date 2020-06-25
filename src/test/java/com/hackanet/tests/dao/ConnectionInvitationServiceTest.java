package com.hackanet.tests.dao;

import com.hackanet.models.ConnectionInvitation;
import com.hackanet.models.enums.ConnectionInvitationStatus;
import com.hackanet.models.user.User;
import com.hackanet.repositories.ConnectionInvitationRepository;
import com.hackanet.services.ConnectionInvitationService;
import com.hackanet.services.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/16/20
 */
public class ConnectionInvitationServiceTest extends AbstractDaoTest {

    @Autowired
    private ConnectionInvitationService connectionInvitationService;

    @Autowired
    private ConnectionInvitationRepository connectionInvitationRepository;

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
        clearUpTables("connection_invitations", "users", "files_info");
        executeScripts("file_info_data");
        executeScripts("user_data");
    }

    @Test
    public void sendInvitationTest() {
        final User user = TestEntityCreator.getUser(1L);
        final User userToInvite = TestEntityCreator.getUser(2L);
        BDDMockito.given(userService.get(2L)).willReturn(userToInvite);
        final ConnectionInvitation connectionInvitation = TestEntityCreator.getConnectionInvitation(1L, user, userToInvite);
        final ConnectionInvitation actual = connectionInvitationService.sendInvitation(user, userToInvite.getId());
        assertEquals(connectionInvitation, actual);
    }

    @Test
    @Transactional
    public void changeStatusTest() {
        final User user = TestEntityCreator.getUser(1L);
        final User userToInvite = TestEntityCreator.getUser(2L);
        BDDMockito.given(userService.get(2L)).willReturn(userToInvite);
        ConnectionInvitation connectionInvitation = TestEntityCreator.getConnectionInvitation(1L, user, userToInvite);
        connectionInvitation = connectionInvitationRepository.save(connectionInvitation);
        final ConnectionInvitationStatus status = new Random().nextBoolean() ? ConnectionInvitationStatus.ACCEPTED : ConnectionInvitationStatus.REJECTED;
        connectionInvitation.setStatus(status);
        final ConnectionInvitation actual = connectionInvitationService.changeStatus(userToInvite, connectionInvitation.getId(), status);
        assertConnectionInvitationsEqual(connectionInvitation, actual);
    }

    @Test
    @Transactional
    //does not pass
    public void deleteTest() {
        final User user = TestEntityCreator.getUser(1L);
        final User userToInvite = TestEntityCreator.getUser(2L);
        BDDMockito.given(userService.get(2L)).willReturn(userToInvite);
        ConnectionInvitation connectionInvitation = TestEntityCreator.getConnectionInvitation(1L, user, userToInvite);
        connectionInvitation = connectionInvitationRepository.save(connectionInvitation);
        final List<Long> longs = jdbcTemplate.queryForList("select connection_invitations.user_id from connection_invitations", Long.class);
        connectionInvitationService.delete(connectionInvitation.getInvitedUser().getId(), connectionInvitation.getUser().getId());
        final Long count = jdbcTemplate.queryForObject("select count(*) from connection_invitations", Long.class);
        final List<ConnectionInvitation> query = jdbcTemplate.query("select * from connection_invitations", new BeanPropertyRowMapper<>(ConnectionInvitation.class));
        assertEquals(Optional.of(0L), Optional.of(count));
    }

    // overriding of equals and hashcode in ConnectionInvitation class causes LazyInitException
    // to prevent the exception from being thrown this method was created
    private void assertConnectionInvitationsEqual(ConnectionInvitation connectionInvitation, ConnectionInvitation actual) {
        assertEquals(connectionInvitation.getId(), actual.getId());
        assertEquals(connectionInvitation.getStatus(), actual.getStatus());
        assertEquals(connectionInvitation.getUser(), actual.getUser());
        assertEquals(connectionInvitation.getInvitedUser(), actual.getInvitedUser());
    }
}
