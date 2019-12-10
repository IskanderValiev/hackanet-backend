package com.hackanet.controllers;

import com.hackanet.json.dto.HackathonDto;
import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonSearchForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import com.hackanet.json.forms.JoinToHackathonRequestCreateForm;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;
import com.hackanet.services.HackathonProfileViewService;
import com.hackanet.services.HackathonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/18/19
 */
@RestController
@RequestMapping("/hackathons")
@Api(tags = "Hackathon Controller")
public class HackathonController {

    static final String HACKATHON = "/{id}";
    private static final String LIST = "/list";
    private static final String VIEWS = HACKATHON + "/views";
    private static final String FRIENDS = "/friends";

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    @Qualifier("hackathonMapper")
    private Mapper<Hackathon, HackathonDto> mapper;

    @Autowired
    private HackathonProfileViewService hackathonProfileViewService;

    @GetMapping
    @ApiOperation(value = "Get all hackathons")
    public ResponseEntity<List<HackathonDto>> getAll() {
        List<Hackathon> hackathons = hackathonService.getAll();
        return new ResponseEntity<>(mapper.map(hackathons), HttpStatus.OK);
    }

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Add new hackathon")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<HackathonDto> add(@Valid @RequestBody HackathonCreateForm form,
                                            @AuthenticationPrincipal User user) {
        Hackathon hackathon = hackathonService.save(user, form);
        return new ResponseEntity<>(mapper.map(hackathon), HttpStatus.CREATED);
    }

    @GetMapping(HACKATHON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = false, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get the information about hackathons")
    public ResponseEntity<HackathonDto> get(@PathVariable Long id,
                                            @AuthenticationPrincipal User user) {
        Hackathon hackathon = hackathonService.get(id);
        hackathonProfileViewService.addView(user, hackathon);
        return new ResponseEntity<>(mapper.map(hackathon), HttpStatus.OK);
    }

    @PutMapping(HACKATHON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Update the hackathon information")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<HackathonDto> update(@PathVariable Long id,
                                               @Valid @RequestBody HackathonUpdateForm form,
                                               @AuthenticationPrincipal User user) {
        Hackathon hackathon = hackathonService.update(id, user, form);
        return ResponseEntity.ok(mapper.map(hackathon));
    }

    @DeleteMapping(HACKATHON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Delete the hackathon")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        hackathonService.delete(id, user);
        return ResponseEntity.ok("OK");
    }

    @PostMapping(LIST)
    @ApiOperation(value = "Search hackathons")
    public ResponseEntity<List<HackathonDto>> list(@RequestBody HackathonSearchForm form) {
        List<Hackathon> hackathons = hackathonService.hackathonList(form);
        return ResponseEntity.ok(mapper.map(hackathons));
    }

    @GetMapping(VIEWS)
    @ApiOperation(value = "Get information about views in the perion")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Long> getViewsCount(@AuthenticationPrincipal User user,
                                              @RequestParam Date from,
                                              @RequestParam Date to,
                                              @PathVariable Long id) {
        return ResponseEntity.ok(hackathonProfileViewService.countsOfUniqueViewInPeriod(user, id, from, to));
    }

    @GetMapping(FRIENDS)
    @ApiOperation(value = "Get friends hackathons")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HackathonDto>> getFriendsHackathons(@AuthenticationPrincipal User user) {
        List<Hackathon> hackathons = hackathonService.getFriendsHackathons(user);
        return ResponseEntity.ok(mapper.map(hackathons));
    }
}
