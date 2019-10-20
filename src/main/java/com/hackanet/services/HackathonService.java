package com.hackanet.services;

import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;

import java.util.List;

public interface HackathonService {
    List<com.hackanet.models.Hackathon> getAll();
    Hackathon save(User user, HackathonCreateForm form);
    Hackathon get(Long id);
    Hackathon update(Long id, User user, HackathonUpdateForm form);
    void delete(Long id, User user);
}
