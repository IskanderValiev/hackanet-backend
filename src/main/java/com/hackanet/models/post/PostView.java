package com.hackanet.models.post;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.user.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/3/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PostView extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    private Timestamp timestamp;
}
