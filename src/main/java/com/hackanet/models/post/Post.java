package com.hackanet.models.post;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.FileInfo;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.PostImportance;
import com.hackanet.models.hackathon.Hackathon;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends AbstractEntity {

    @Column(nullable = false)
    private String title;

    // TODO: 3/22/20 change this
    @Column(nullable = false, length = 1024)
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    private FileInfo picture;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private List<FileInfo> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private PostImportance importance;

    @OneToMany(mappedBy = "post")
    private List<PostLike> likes;
}
