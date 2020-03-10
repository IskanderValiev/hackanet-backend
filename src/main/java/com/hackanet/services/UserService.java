package com.hackanet.services;

import com.google.common.collect.Multimap;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.*;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;
import com.hackanet.models.UserPhoneToken;
import com.hackanet.push.enums.ClientType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;
import java.util.Set;

public interface UserService extends CrudService<User> {
    TokenDto register(UserRegistrationForm form);
    TokenDto login(UserLoginForm form);
    Boolean exists(String email);
    Boolean existsByPhone(String phone);
    User get(String email);
    User getUserInfo(Long id, User currentUser);
    Set<User> getByIds(List<Long> ids);
    List<User> userList(UserSearchForm form);
    void updateUsersHackathonList(User user, Hackathon hackathon, boolean add);
    User update(Long id, User currentUser, UserUpdateForm form);
    Multimap<ClientType, UserPhoneToken> getTokensForUser(Long userId);
    User createForCompany(CompanyCreateForm form);
    TokenDto getTokenDtoFromString(String string);
    User get(User jwtData);
}
