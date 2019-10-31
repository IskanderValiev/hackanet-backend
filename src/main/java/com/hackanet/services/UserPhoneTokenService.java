package com.hackanet.services;

import com.hackanet.json.forms.UserPhoneTokenAddForm;
import com.hackanet.models.UserPhoneToken;

public interface UserPhoneTokenService {
    UserPhoneToken add(UserPhoneTokenAddForm form);
}
