package com.hackanet.models;

import com.hackanet.models.hackathon.Hackathon;
import lombok.*;

import javax.persistence.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "files_info")
public class FileInfo extends AbstractEntity {

    private String name;

    private Long size;

    @Column(name = "preview_link", unique = true, nullable = false)
    private String previewLink;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "logo")
    private Hackathon hackathonImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User user;

    private String type;
}
