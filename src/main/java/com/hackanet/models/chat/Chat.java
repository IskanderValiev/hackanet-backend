package com.hackanet.models.chat;

import com.google.common.collect.Sets;
import com.hackanet.models.AbstractEntity;
import com.hackanet.models.team.Team;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.User;
import com.hackanet.models.enums.ChatType;
import lombok.*;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chats")
public class Chat extends AbstractEntity {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "chat_participants_table",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id"))
    private Set<User> participants;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "chats_admins",
    joinColumns = {@JoinColumn(name = "admin_id")},
    inverseJoinColumns = {@JoinColumn(name = "chat_id")})
    private Set<User> admins;
    @OneToMany(mappedBy = "chat", orphanRemoval = true)
    private List<ChatMessage> messages;
    @Enumerated(EnumType.STRING)
    private ChatType type;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "chat")
    private Team team;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    public Set<User> getParticipants() {
        return participants == null ? Collections.emptySet() : participants;
    }

    public Set<User> getAdmins() {
        return admins == null ? Sets.newHashSet() : admins;
    }
}
