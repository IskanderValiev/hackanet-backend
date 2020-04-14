package com.hackanet.controllers;

import com.hackanet.json.dto.JobOfferDto;
import com.hackanet.json.mappers.JobOfferMapper;
import com.hackanet.models.JobOffer;
import com.hackanet.models.user.User;
import com.hackanet.services.JobOfferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/28/19
 */
@RestController
@Api(tags = "Job Offers Controller")
@RequestMapping("/job/offers")
public class JobOfferController {

    private static final String SEND = "/send";
    private static final String ONE_INVITATION = "/{id}";
    private static final String DELETE_FOR_USER = ONE_INVITATION + "/user";
    private static final String ACCEPT = ONE_INVITATION + "/accept";

    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private JobOfferMapper jobOfferMapper;

    @GetMapping(SEND)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Send a job offer")
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<JobOfferDto> send(@RequestParam Long userId,
                                            @AuthenticationPrincipal User user) {
        JobOffer invitation = jobOfferService.create(user, userId);
        return new ResponseEntity<>(jobOfferMapper.map(invitation), HttpStatus.CREATED);
    }

    @DeleteMapping(ONE_INVITATION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Delete a job offer")
    @PreAuthorize("hasAnyAuthority('COMPANY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        jobOfferService.delete(user, id);
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping(DELETE_FOR_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Delete a job offer for user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JobOfferDto> deleteForUser(@PathVariable Long id,
                                                     @AuthenticationPrincipal User user) {
        JobOffer invitation = jobOfferService.deleteForUser(user, id);
        return ResponseEntity.ok(jobOfferMapper.map(invitation));
    }

    @GetMapping(ACCEPT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Accept a job offer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> accept(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        jobOfferService.accept(user, id);
        return ResponseEntity.ok("Job Offer Accepted");
    }

}
