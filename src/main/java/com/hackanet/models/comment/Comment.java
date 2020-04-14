package com.hackanet.models.comment;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.post.Post;
import com.hackanet.models.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "comments")
public class Comment extends AbstractEntity {

    @Column(name = "text", length = 300, nullable = false)
    private String text;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment")
    private List<CommentLike> likes;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment replyParent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "replyParent")
    private List<Comment> replies;

    @Column(name = "edited", nullable = false)
    private Boolean edited;
}
