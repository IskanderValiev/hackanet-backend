package com.hackanet.controllers;

import com.hackanet.json.dto.HackathonDto;
import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;
import com.hackanet.services.HackathonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    private static final String HACKATHON = "/{id}";

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    @Qualifier("hackathonMapper")
    private Mapper<Hackathon, HackathonDto> mapper;

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
    public ResponseEntity<HackathonDto> add(@RequestBody HackathonCreateForm form,
                                            @AuthenticationPrincipal User user) {
        Hackathon hackathon = hackathonService.save(user, form);
        return new ResponseEntity<>(mapper.map(hackathon), HttpStatus.CREATED);
    }

    @GetMapping(HACKATHON)
    @ApiOperation(value = "Get all hackathons")
    public ResponseEntity<HackathonDto> get(@PathVariable Long id) {
        Hackathon hackathon = hackathonService.get(id);
        return new ResponseEntity<>(mapper.map(hackathon), HttpStatus.OK);
    }

    @PutMapping(HACKATHON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Update the hackathon information")
    public ResponseEntity<HackathonDto> update(@PathVariable Long id,
                                               @RequestBody HackathonUpdateForm form,
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
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        hackathonService.delete(id, user);
        return ResponseEntity.ok("OK");
    }
}
