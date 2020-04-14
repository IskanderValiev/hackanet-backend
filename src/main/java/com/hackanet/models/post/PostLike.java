package com.hackanet.models.post;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.LikeType;
import lombok.*;

import javax.persistence.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/2/19
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PostLike extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private LikeType likeType;
}
