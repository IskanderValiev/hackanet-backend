package com.hackanet.services.user;

import com.google.common.collect.Multimap;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.*;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserPhoneToken;
import com.hackanet.push.enums.ClientType;
import com.hackanet.services.RetrieveService;

import java.util.List;
import java.util.Set;

public interface UserService extends RetrieveService<User> {
    TokenDto register(UserRegistrationForm form);
    TokenDto login(UserLoginForm form);
    Boolean exists(String email);
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
    void updateLastRequestTime(User user);
    void confirmEmail(String code);
}
