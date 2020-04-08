package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    @Pattern(regexp = "[0-9a-zA-Z\\-_]+", message = "Nickname can be containing only letters, numbers, - and _")
    private String nickname;

    private Long picture;

    private String about;

    private String country;

    private String city;

    private List<Long> skills;

    private Long positionId;

    private Boolean lookingForTeam;

    private String university;

    public Long getPicture() {
        return picture == null ? 1 : picture;
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
