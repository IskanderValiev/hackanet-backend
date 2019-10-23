package com.hackanet.services;

import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.json.forms.UserSearchForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;

import java.util.List;

public interface UserService {
    TokenDto register(UserRegistrationForm form);
    TokenDto login(UserLoginForm form);
    Boolean exists(String email);
    Boolean existsByPhone(String phone);
    User get(String email);
    User get(Long id);
    List<User> userList(UserSearchForm form);
    void updateUsersHackathonList(User user, Hackathon hackathon, boolean add);
}
