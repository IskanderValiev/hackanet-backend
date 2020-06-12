package com.hackanet.tests.dao.user;

import com.hackanet.exceptions.AlreadyExistsException;
import com.hackanet.models.user.Position;
import com.hackanet.repositories.user.PositionRepository;
import com.hackanet.services.user.PositionService;
import com.hackanet.tests.dao.AbstractDaoTest;
import com.hackanet.tests.dao.TestEntityCreator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/12/20
 */
public class PositionServiceTest extends AbstractDaoTest {

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionRepository positionRepository;

    @Override
    @Before
    public void init() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }

    @Override
    protected void prepareDatabase() {
        clearUpTable("positions");
        executeScripts("position_data");
    }

    @Test
    public void createTest() {
        String name = "backend developer";
        final Position position = TestEntityCreator.getPosition(1L, name);
        final Position actual = positionService.create(name);
        assertEquals(position.getName(), actual.getName());
        assertThrows(AlreadyExistsException.class, () -> positionService.create(name));
    }

    @Test
    public void updateTest() {
        prepareDatabase();
        final Position position = TestEntityCreator.getPosition(1L, "Frontend developer");
        final Position actual = positionService.update(1L, "Frontend developer");
        assertEquals(position, actual);
    }

    @Test
    public void deleteTest() {
        prepareDatabase();
        positionService.delete(1L);
        final Integer count = jdbcTemplate.queryForObject("select count(*) from positions", Integer.class);
        assertEquals(0, count);
    }
}
