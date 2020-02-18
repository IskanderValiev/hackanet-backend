package com.hackanet.services;

import com.hackanet.exceptions.BadFormTypeException;
import com.hackanet.json.forms.CreateForm;
import com.hackanet.json.forms.UpdateForm;

public interface FormValidatorService {
    default void validateCreateForm(CreateForm form) {
        throw new BadFormTypeException("Invalid type of the create form");
    }

    default void validateUpdateForm(UpdateForm form) {
        throw new BadFormTypeException("Invalid type of the update form");
    }
}
