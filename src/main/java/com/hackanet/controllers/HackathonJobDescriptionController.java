package com.hackanet.controllers;

import com.hackanet.json.dto.HackathonJobDescriptionDto;
import com.hackanet.json.forms.HackathonJobDescriptionCreateForm;
import com.hackanet.json.mappers.HackathonJobDescriptionMapper;
import com.hackanet.models.hackathon.HackathonJobDescription;
import com.hackanet.models.user.User;
import com.hackanet.services.hackathon.HackathonJobDescriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@RestController
@Api(tags = "Hackathon Job Description Controller")
@RequestMapping("/hackathon/job/description")
public class HackathonJobDescriptionController {

    private static final String ADD = "/user/{id}";

    @Autowired
    private HackathonJobDescriptionService hackathonJobDescriptionService;
    @Autowired
    private HackathonJobDescriptionMapper hackathonJobDescriptionMapper;

    @PostMapping(ADD)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Add job description for user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HackathonJobDescriptionDto> add(@PathVariable Long id,
                                                          @RequestBody HackathonJobDescriptionCreateForm form,
                                                          @AuthenticationPrincipal User user) {
        HackathonJobDescription forPortfolio = hackathonJobDescriptionService.createForPortfolio(form);
        return ResponseEntity.ok(hackathonJobDescriptionMapper.map(forPortfolio));
    }
}
