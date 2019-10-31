package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.push.enums.ClientType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserPhoneTokenAddForm {
    @NotNull
    @ApiModelProperty(required = true)
    private ClientType clientType;
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true)
    private String token;
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true)
    private String deviceId;
    @ApiModelProperty(hidden = true)
    private Long userId;
}
