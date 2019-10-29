package com.hackanet.controllers;

import com.hackanet.json.dto.HackathonDto;
import com.hackanet.json.dto.JoinToHackathonRequestDto;
import com.hackanet.json.forms.JoinToHackathonRequestCreateForm;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.JoinToHackathonRequest;
import com.hackanet.models.User;
import com.hackanet.models.enums.RequestStatus;
import com.hackanet.services.JoinToHackathonRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hackanet.controllers.HackathonController.HACKATHON;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
@RestController
@Api(tags = "Join To Hackathon Request Controller")
@RequestMapping("/join/requests")
public class JoinToHackathonRequestController {

    private static final String JOIN_TO_HACKATHON = HACKATHON + "/register";
    private static final String REQUESTS_BY_HACKATHON_ID = "/hackathon" + HACKATHON;
    private static final String STATUS = "/{id}/status";

    @Autowired
    private JoinToHackathonRequestService service;
    @Autowired
    private Mapper<JoinToHackathonRequest, JoinToHackathonRequestDto> mapper;

    @PostMapping(JOIN_TO_HACKATHON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Join to hackathon")
    @PreAuthorize("isAuthenticated() || hasAuthority('USER')")
    public ResponseEntity<JoinToHackathonRequestDto> join(@PathVariable Long id,
                                                          @AuthenticationPrincipal User user,
                                                          @RequestBody JoinToHackathonRequestCreateForm form) {
        form.setHackathonId(id);
        JoinToHackathonRequest request = service.createRequest(form, user);
        return ResponseEntity.ok(mapper.map(request));
    }

    @GetMapping(REQUESTS_BY_HACKATHON_ID)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get all requests by hackathon id")
    @PreAuthorize("isAuthenticated() || hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<JoinToHackathonRequestDto>> getAllByHackathonId(@PathVariable Long id,
                                                                               @AuthenticationPrincipal User user) {
        List<JoinToHackathonRequest> requests = service.getAllByHackathonId(id, user);
        return ResponseEntity.ok(mapper.map(requests));
    }

    @GetMapping(STATUS)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get all requests by hackathon id")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<JoinToHackathonRequestDto> changeStatus(@RequestParam RequestStatus status,
                                                                  @PathVariable Long id,
                                                                  @AuthenticationPrincipal User user) {
        JoinToHackathonRequest request = service.changeStatus(id, user, status);
        return ResponseEntity.ok(mapper.map(request));
    }

}
