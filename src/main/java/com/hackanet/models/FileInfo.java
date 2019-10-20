package com.hackanet.models;

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
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Long size;
    @Column(name = "preview_link", unique = true, nullable = false)
    private String previewLink;
    private Integer height;
    private Integer width;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "logo")
    private Hackathon hackathonImage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User user;
    private String type;
}
