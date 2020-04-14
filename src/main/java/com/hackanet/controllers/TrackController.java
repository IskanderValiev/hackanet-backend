package com.hackanet.controllers;

import com.hackanet.json.dto.TrackDto;
import com.hackanet.json.forms.TrackCreateForm;
import com.hackanet.json.forms.TrackUpdateForm;
import com.hackanet.json.mappers.TrackMapper;
import com.hackanet.models.hackathon.Track;
import com.hackanet.models.user.User;
import com.hackanet.services.hackathon.TrackService;
import io.swagger.annotations.Api;
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
 * on 3/20/20
 */
@RestController
@Api(tags = "Track Controller")
@RequestMapping("/tracks")
public class TrackController {

    private static final String ONE = "/{id}";
    private static final String BY_HACKATHON = "/hackathon/{id}";

    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private TrackService trackService;

    @PostMapping
    @ApiOperation(value = "Add a new track")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<TrackDto> add(@Valid @RequestBody TrackCreateForm form,
                                        @AuthenticationPrincipal User user,
                                        HttpServletRequest request) {
        Track track = trackService.create(user, form);
        return new ResponseEntity<>(trackMapper.map(track), HttpStatus.CREATED);
    }

    @GetMapping(ONE)
    @ApiOperation(value = "Get information about the track")
    public ResponseEntity<TrackDto> getOne(@PathVariable Long id,
                                           @AuthenticationPrincipal User user,
                                           HttpServletRequest request) {
        Track track = trackService.get(id);
        return ResponseEntity.ok(trackMapper.map(track));
    }

    @PutMapping(ONE)
    @ApiOperation(value = "Update the track information")
    public ResponseEntity<TrackDto> update(@Valid @RequestBody TrackUpdateForm form,
                                           @PathVariable Long id,
                                           @AuthenticationPrincipal User user,
                                           HttpServletRequest request) {
        Track update = trackService.update(user, id, form);
        return ResponseEntity.ok(trackMapper.map(update));
    }

    @GetMapping(BY_HACKATHON)
    @ApiOperation(value = "Get tracks by hackathon id")
    public ResponseEntity<List<TrackDto>> getByHackathon(@PathVariable Long id,
                                                         @AuthenticationPrincipal User user,
                                                         HttpServletRequest request) {
        List<Track> tracks = trackService.getByHackathonId(id);
        return ResponseEntity.ok(trackMapper.map(tracks));
    }

}
