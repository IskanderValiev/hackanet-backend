package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.application.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserUpdateForm {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String lastname;

    private Long image;

    @NotNull
    @NotEmpty
    private String about;

    @NotNull
    @NotEmpty
    private String country;

    @NotNull
    @NotEmpty
    private String city;

    private List<Long> skills;

    private Boolean lookingForTeam;

    public Long getImage() {
        // TODO: 1/21/20 return default value if image is null
        return image;
    }

    public List<Long> getSkills() {
        if (skills == null)
            skills = new ArrayList<>();
        return skills;
    }

    public Boolean getLookingForTeam() {
        return Boolean.TRUE.equals(lookingForTeam);
    }
}
