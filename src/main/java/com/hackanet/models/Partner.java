package com.hackanet.models;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/27/19
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "partners")
public class Partner extends AbstractEntity{

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private FileInfo logo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "hackathon_partners",
            joinColumns = @JoinColumn(name = "partner_id"),
            inverseJoinColumns = @JoinColumn(name = "hackathon_id"))
    private Set<Hackathon> hackathons;

    private String link;
}
