package com.hackanet.services;

import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonSearchForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;

import java.util.List;

public interface HackathonService extends CrudService<Hackathon> {
    Hackathon save(User user, HackathonCreateForm form);
    Hackathon save(Hackathon hackathon);
    Hackathon update(Long id, User user, HackathonUpdateForm form);
    void delete(Long id, User user);
    List<Hackathon> hackathonList(HackathonSearchForm form);
    void updateUsersHackathonList(List<User> users, Hackathon hackathon, boolean add);
    List<Hackathon> getFriendsHackathons(User user);
    List<Hackathon> getHackathonsListByUser(User user);
}
