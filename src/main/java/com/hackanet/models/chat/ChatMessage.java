package com.hackanet.models.chat;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.user.User;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "sender_id")
    private User sender;
    @Column(nullable = false)
    private Timestamp messageTime;
    @Column(nullable = false, length = 1000)
    private String text;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "chat_id")
    private Chat chat;
    // TODO: 10/24/19 add attachments feature
}
