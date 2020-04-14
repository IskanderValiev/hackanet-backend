package com.hackanet.models.comment;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.LikeType;
import lombok.*;

import javax.persistence.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comments_likes")
public class CommentLike extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private LikeType type;
}
