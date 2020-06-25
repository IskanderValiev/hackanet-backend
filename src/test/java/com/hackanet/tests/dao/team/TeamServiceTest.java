package com.hackanet.tests.dao.team;

import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.models.enums.TeamType;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.repositories.TeamRepository;
import com.hackanet.services.team.TeamService;
import com.hackanet.tests.dao.AbstractDaoTest;
import com.hackanet.tests.dao.TestEntityCreator;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/10/20
 */
public class TeamServiceTest extends AbstractDaoTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;



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
        clearUpTables("team", "users", "files_info");
        executeScripts("file_info_data");
        executeScripts("user_data");
    }

    @Test
    public void createTeamTest() {
        TeamCreateForm teamCreateForm = new TeamCreateForm();
        teamCreateForm.setName("test_name");
        teamCreateForm.setTeamType(TeamType.CONSTANT);
        teamCreateForm.setParticipantsIds(Lists.newArrayList(1L, 2L, 3L));
        teamCreateForm.setTeamLeaderUsedSkills(Lists.newArrayList(1L));
        final User user = TestEntityCreator.getUser(1L);
        final Team team = TestEntityCreator.getTeam(1L, 1L, Lists.newArrayList(1L, 2L, 3L));
        final Team actual = teamService.createTeam(user, teamCreateForm);
        assertEquals(team.getId(), actual.getId());
        assertEquals(team.getTeamType(), actual.getTeamType());
    }
}
