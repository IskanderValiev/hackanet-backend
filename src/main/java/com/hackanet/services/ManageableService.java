package com.hackanet.services;

import com.hackanet.json.forms.CreateForm;
import com.hackanet.json.forms.UpdateForm;
import com.hackanet.models.AbstractEntity;
import com.hackanet.models.User;

import java.util.Collections;
import java.util.List;

public interface ManageableService<T extends AbstractEntity> {
    default T add(User user, CreateForm createForm) {
        return null;
    }
    T update(Long id, User user, UpdateForm updateForm);
    T get(Long id);
    default List<T> getAll() {
        return Collections.emptyList();
    }
}
