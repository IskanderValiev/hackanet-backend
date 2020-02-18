package com.hackanet.services;

import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonSearchForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;

import java.util.List;

public interface HackathonService extends ManageableService<Hackathon>, FormValidatorService {
    Hackathon save(User user, HackathonCreateForm form);
    Hackathon save(Hackathon hackathon);
    void delete(Long id, User user);
    List<Hackathon> hackathonList(HackathonSearchForm form);
    void updateUsersHackathonList(List<User> users, Hackathon hackathon, boolean add);
    List<Hackathon> getFriendsHackathons(User user);
}
