package com.hackanet.services;

import com.hackanet.json.forms.SponsorUpdateForm;
import com.hackanet.json.forms.SponsorCreateForm;
import com.hackanet.models.User;
import com.hackanet.models.hackathon.Sponsor;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/23/20
 */
public interface SponsorService extends RetrieveService<Sponsor> {
    Sponsor create(User user, SponsorCreateForm form);
    Sponsor update(User user, Long id, SponsorUpdateForm form);
    void delete(User user, Long id);
    List<Sponsor> getByHackathonId(Long hackthondId);
}
