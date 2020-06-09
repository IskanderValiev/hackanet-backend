package com.hackanet.tests;

import com.hackanet.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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

    protected void executeScripts(Resource resource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, resource);
            log.info("The script from " + resource.getFilename() + " has been executed successfully.");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
