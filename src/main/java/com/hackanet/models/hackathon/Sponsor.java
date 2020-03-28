package com.hackanet.models.hackathon;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.FileInfo;
import lombok.*;

import javax.persistence.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/23/20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sponsors")
public class Sponsor extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private FileInfo logo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Hackathon hackathon;

    @Column(name = "link", nullable = true)
    private String link;
}
