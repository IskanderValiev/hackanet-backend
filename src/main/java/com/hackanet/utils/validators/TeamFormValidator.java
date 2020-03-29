package com.hackanet.utils.validators;

import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.utils.SwearWordsFilter;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/29/20
 */
@Service
public class TeamFormValidator implements FormValidator<TeamCreateForm, TeamUpdateForm> {

    @Override
    public void validateCreateForm(TeamCreateForm createForm) {
        checkArgument(SwearWordsFilter.containsBadWords(createForm.getName().trim()), "Team name contains bad words");
    }

    @Override
    public void validateUpdateForm(TeamUpdateForm updateForm) {
        checkArgument(SwearWordsFilter.containsBadWords(updateForm.getName().trim()), "Team name contains bad words");
    }
}
