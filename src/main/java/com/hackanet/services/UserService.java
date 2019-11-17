package com.hackanet.services;

import com.google.common.collect.Multimap;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.*;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;
import com.hackanet.models.UserPhoneToken;
import com.hackanet.models.UserToken;
import com.hackanet.push.enums.ClientType;

import java.util.List;
import java.util.Map;

public interface UserService extends CrudService<User> {
    TokenDto register(UserRegistrationForm form);
    TokenDto login(UserLoginForm form);
    Boolean exists(String email);
    Boolean existsByPhone(String phone);
    User get(String email);
    List<User> getByIds(List<Long> ids);
    List<User> userList(UserSearchForm form);
    void updateUsersHackathonList(User user, Hackathon hackathon, boolean add);
    TokenDto saveFromGoogle(Map<String, Object> userDetails);
    User update(Long id, User currentUser, UserUpdateForm form);
    Multimap<ClientType, UserPhoneToken> getTokensForUser(Long userId);
    void passwordResetRequest(String email);
    void changePassword(String code, String newPassword, String email);
    User createForCompany(CompanyCreateForm form);
    TokenDto updateAccessToken(User user);
    UserToken getByUserId(Long userId);
    boolean userTokenExpired(UserToken token, boolean isRefreshToken);
    TokenDto getTokenDtoFromString(String string);
}
