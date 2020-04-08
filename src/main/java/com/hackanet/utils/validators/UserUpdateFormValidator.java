package com.hackanet.utils.validators;

import com.google.common.base.Preconditions;
import com.hackanet.json.forms.UserUpdateForm;
import com.hackanet.models.FileInfo;
import com.hackanet.services.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/8/20
 */
@Service
public class UserUpdateFormValidator implements UpdateFormValidator<UserUpdateForm> {
    
    @Autowired
    private FileInfoService fileInfoService;

    @Override
    public void validateUpdateForm(UserUpdateForm form) {
        Pattern p = Pattern.compile("[\\p{L}]+", Pattern.UNICODE_CHARACTER_CLASS);
        final Matcher nameMatcher = p.matcher(form.getName());
        Preconditions.checkArgument(nameMatcher.matches(), "Name must be containing only letters");
        final Matcher lastnameMatcher = p.matcher(form.getLastname());
        Preconditions.checkArgument(lastnameMatcher.matches(), "Last name must be containing only letter");
        if (form.getCountry() != null) {
            final Matcher matcher = p.matcher(form.getCountry());
            Preconditions.checkArgument(matcher.matches(), "County must be containing only letters");
        }
        if (form.getCity() != null) {
            final Matcher matcher = p.matcher(form.getCity());
            Preconditions.checkArgument(matcher.matches(), "City must be containing only letters");
        }
        final FileInfo fileInfo = fileInfoService.get(form.getPicture());
        Preconditions.checkArgument(fileInfoService.isImage(fileInfo), "The file is not an image");
    }
}
