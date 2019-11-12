package com.hackanet.models.chat;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "chat_settings")
public class ChatSettings extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;
    private Boolean muted = false;
}
