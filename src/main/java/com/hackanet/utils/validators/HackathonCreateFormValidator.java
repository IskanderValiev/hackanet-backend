package com.hackanet.utils.validators;

import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.System.currentTimeMillis;

/**
 * @author Iskander Valiev
 * created by isko
 * on 1/3/20
 */
@Service
public class HackathonCreateFormValidator implements FormValidator<HackathonCreateForm, HackathonUpdateForm> {

    @Override
    public void validateCreateForm(HackathonCreateForm createForm) {
        Date start = new Date(createForm.getStart());
        Date end = new Date(createForm.getEnd());
        checkArgument(start.after(end), "Start date is after end date");
    }

    @Override
    public void validateUpdateForm(HackathonUpdateForm updateForm) {
        Date start = new Date(updateForm.getStartDate());
        Date end = new Date(updateForm.getEndDate());
        checkArgument(start.after(end), "Start date is after end date");
        checkArgument(start.before(new Date(currentTimeMillis())), "Start date is in the past");

        Long regStartDate = updateForm.getRegistrationStartDate();
        Long regEndDate = updateForm.getRegistrationEndDate();
        checkArgument(regStartDate > regEndDate, "Registration Start Date is after End Date");
        checkArgument(regEndDate < System.currentTimeMillis(), "Registration End Date is in the past");
        checkArgument(new Timestamp(regStartDate).after(start), "Registration Start Date must be before hackathon start date");
        checkArgument(new Timestamp(regEndDate).after(end), "Registration End Date must be before hackathon end date");
    }
}
