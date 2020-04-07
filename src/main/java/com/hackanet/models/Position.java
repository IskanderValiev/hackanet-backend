package com.hackanet.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/7/20
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "positions")
public class Position extends AbstractEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
