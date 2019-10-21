package com.hackanet.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "skills")
public class Skill extends AbstractEntity {
    @Column(nullable = false, unique = true)
    private String name;
    private String nameLc;
}
