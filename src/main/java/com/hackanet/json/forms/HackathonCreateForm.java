package com.hackanet.json.forms;

import com.hackanet.models.User;
import com.neovisionaries.i18n.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.sql.Date;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HackathonCreateForm {
    private String name;
    private Date start;
    private Date end;
    private Long logoId;
    private String description;
    private String country;
    private String city;
    private List<Long> requiredSkills;
}
