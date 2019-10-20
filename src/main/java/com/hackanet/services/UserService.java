package com.hackanet.services;

import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.models.User;

public interface UserService {
    TokenDto register(UserRegistrationForm form);
    TokenDto login(UserLoginForm form);
    Boolean exists(String email);
    User get(String email);
}
