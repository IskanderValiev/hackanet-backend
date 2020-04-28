package com.hackanet.controllers;

import com.hackanet.json.dto.JobExperienceDto;
import com.hackanet.json.forms.JobExperienceCreateForm;
import com.hackanet.json.forms.JobExperienceUpdateForm;
import com.hackanet.json.mappers.JobExperienceMapper;
import com.hackanet.models.JobExperience;
import com.hackanet.models.user.User;
import com.hackanet.services.JobExperienceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@RestController
@Api(tags = "Job experience controller")
@RequestMapping("/job/experience")
public class JobExperienceController {

    private static final String JOB_EXPERIENCE = "/{id}";

    @Autowired
    private JobExperienceService jobExperienceService;

    @Autowired
    private JobExperienceMapper jobExperienceMapper;

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Add experience of working")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JobExperienceDto> add(@Valid @RequestBody JobExperienceCreateForm form,
                                                @AuthenticationPrincipal User user) {
        JobExperience forPortfolio = jobExperienceService.addForPortfolio(user, form);
        return ResponseEntity.ok(jobExperienceMapper.map(forPortfolio));
    }

    @DeleteMapping(JOB_EXPERIENCE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Delete experience of working")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> add(@PathVariable Long id,
                                      @AuthenticationPrincipal User user) {
        jobExperienceService.delete(user, id);
        return ResponseEntity.ok("OK");
    }

    @PutMapping(JOB_EXPERIENCE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Update experience of working")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JobExperienceDto> add(@PathVariable Long id,
                                                @RequestBody JobExperienceUpdateForm form,
                                                @AuthenticationPrincipal User user) {
        JobExperience jobExperience = jobExperienceService.update(id, user, form);
        return ResponseEntity.ok(jobExperienceMapper.map(jobExperience));
    }
}
