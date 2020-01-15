package com.hackanet.utils.validators;

public interface FormValidator<CF, UF> {
    void validateCreateForm(CF createForm);
    void validateUpdateForm(UF updateForm);
}
