package com.hackanet.tests;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/6/20
 */

import com.hackanet.application.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
public class ApplicationContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        System.setProperty("spring.profiles.active", "test");
    }

    @Test
    public void contextLoadsTest() {
        Assert.assertNotNull("The application context should have been loaded", this.applicationContext);
    }
}
