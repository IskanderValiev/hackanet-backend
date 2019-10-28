package com.hackanet.models.chat;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "chat_participants_table",
            joinColumns = @JoinColumn(name = "participant"),
            inverseJoinColumns = @JoinColumn(name = "chat_id"))
    private List<User> participants;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;
    @OneToMany(mappedBy = "chat", orphanRemoval = true)
    private List<ChatMessage> messages;
}
