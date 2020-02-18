package com.hackanet.services;

import com.hackanet.annotations.NotFormatted;
import com.hackanet.exceptions.BadFormTypeException;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.forms.UpdateForm;
import com.hackanet.models.AbstractEntity;
import com.hackanet.models.User;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

import static com.hackanet.utils.StringUtils.formatTitle;

/**
 * @author Iskander Valiev
 * created by isko
 * on 2/13/20
 */
public abstract class AbstractManageableService<T extends AbstractEntity> implements ManageableService<T> {

    public abstract T update(Long id, User user, UpdateForm updateForm);

    public abstract T get(Long id);

    protected void fillUpData(UpdateForm form, T object) {
        Field[] fields = form.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Field companyField = object.getClass().getField(field.getName());
                companyField.setAccessible(true);
                companyField.set(object, getValue(field, form));
            } catch (NoSuchFieldException e) {
                throw new BadFormTypeException(field.getName() + " field not found");
            } catch (IllegalAccessException e) {
                throw new BadRequestException(field.getName() + " cannot be accessed");
            }
        }
    }

    @SneakyThrows
    private Object getValue(Field field, Object object) {
        if (object == null || field == null)
            throw new BadRequestException("The field or the object is null");
        if (field.getType().equals(String.class)) {
            if (field.isAnnotationPresent(NotFormatted.class)) {
                return field.get(object);
            }
            return formatTitle((String) field.get(object));
        }
        return object;
    }

}
