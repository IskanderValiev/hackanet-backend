package com.hackanet.services;

import com.hackanet.models.User;

public interface TemplateService {
    String prepareEmail(User user);
    String prepareWelcomeEmail(User user);
    String test();
}
