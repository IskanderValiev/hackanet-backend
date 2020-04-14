package com.hackanet.services.user;

import com.hackanet.json.forms.UserPhoneTokenAddForm;
import com.hackanet.models.user.UserPhoneToken;

public interface UserPhoneTokenService {
    UserPhoneToken add(UserPhoneTokenAddForm form);
}
