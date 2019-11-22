package com.hackanet.models;

import lombok.*;

import javax.persistence.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/20/19
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_reviews")
public class UserReview extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_user_id")
    private User reviewedUser;

    @Column(nullable = false)

    private Integer mark;

    private String reviewMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private boolean anonymously = false;
}
