package com.hackanet.controllers;

import com.hackanet.json.dto.SponsorDto;
import com.hackanet.json.forms.SponsorCreateForm;
import com.hackanet.json.forms.SponsorUpdateForm;
import com.hackanet.json.mappers.SponsorMapper;
import com.hackanet.models.User;
import com.hackanet.models.hackathon.Sponsor;
import com.hackanet.services.SponsorService;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/23/20
 */
@RestController
@Api(tags = "Sponsor Controller")
@RequestMapping("/sponsors")
public class SponsorController {

    private static final String ONE = "/{id}";
    private static final String BY_HACKATHON = "/hackathons/{hackathonId}";

    @Autowired
    private SponsorService sponsorService;

    @Autowired
    private SponsorMapper sponsorMapper;

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Add a new sponsor")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<SponsorDto> create(@Valid @RequestBody SponsorCreateForm form,
                                             @AuthenticationPrincipal User user,
                                             HttpServletRequest request) {
        Sponsor sponsor = sponsorService.create(user, form);
        return new ResponseEntity<>(sponsorMapper.map(sponsor), HttpStatus.CREATED);
    }

    @GetMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = false, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get an info about sponsor")
    public ResponseEntity<SponsorDto> get(@PathVariable Long id,
                                          @AuthenticationPrincipal User user,
                                          HttpServletRequest request) {
        Sponsor sponsor = sponsorService.get(id);
        return ResponseEntity.ok(sponsorMapper.map(sponsor));
    }

    @PutMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Update an info about sponsor")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<SponsorDto> update(@Valid @RequestBody SponsorUpdateForm form,
                                             @PathVariable Long id,
                                             @AuthenticationPrincipal User user,
                                             HttpServletRequest request) {
        Sponsor update = sponsorService.update(user, id, form);
        return ResponseEntity.ok(sponsorMapper.map(update));
    }

    @GetMapping(BY_HACKATHON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = false, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get a list of sponsors by hackathon")
    public ResponseEntity<List<SponsorDto>> byHackathon(@PathVariable Long hackathonId,
                                                        @AuthenticationPrincipal User user,
                                                        HttpServletRequest request) {
        List<Sponsor> sponsors = sponsorService.getByHackathonId(hackathonId);
        return ResponseEntity.ok(sponsorMapper.map(sponsors));
    }

    @DeleteMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Delete a sponsor")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user,
                                         HttpServletRequest request) {
        sponsorService.delete(user, id);
        return ResponseEntity.ok("OK");
    }
}
