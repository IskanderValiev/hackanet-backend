package com.hackanet.services.hackathon;

import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonSearchForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.user.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.services.RetrieveService;

import java.util.List;

public interface HackathonService extends RetrieveService<Hackathon> {
    Hackathon save(User user, HackathonCreateForm form);
    Hackathon save(Hackathon hackathon);
    Hackathon update(Long id, User user, HackathonUpdateForm form);
    void delete(Long id, User user);
    List<Hackathon> hackathonList(HackathonSearchForm form);
    void updateUsersHackathonList(List<User> users, Hackathon hackathon, boolean add);
    List<Hackathon> getFriendsHackathons(User user);
    List<Hackathon> getHackathonsListByUser(User user);
    void setChats(List<Chat> chats, Hackathon hackathon);
    Hackathon getByAdmin(Long userId);
    Hackathon approve(Long id);
}
