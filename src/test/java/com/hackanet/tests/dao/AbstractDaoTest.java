package com.hackanet.tests.dao;

import com.hackanet.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/7/20
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public abstract class AbstractDaoTest {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected DataSource dataSource;

    protected JdbcTemplate jdbcTemplate;

    public abstract void init();

    protected void executeScripts(String filename) {
        try (Connection connection = dataSource.getConnection()) {
            final String scriptFullName = "classpath:/" + filename + ".sql";
            ScriptUtils.executeSqlScript(connection, applicationContext.getResource(scriptFullName));
            log.info("The script from " + scriptFullName + " has been executed successfully.");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    protected abstract void prepareDatabase();

    protected void clearUpTable(String tableName) {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        jdbcTemplate.execute("delete from " + tableName);
        log.info("{} table has been cleared up", tableName);
    }

    protected void clearUpTables(String... tablesNames) {
        Stream.of(tablesNames).forEach(this::clearUpTable);
    }
}
