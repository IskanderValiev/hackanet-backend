package com.hackanet.controllers;

import com.hackanet.json.dto.PositionDto;
import com.hackanet.json.mappers.PositionMapper;
import com.hackanet.models.user.Position;
import com.hackanet.models.user.User;
import com.hackanet.services.user.PositionService;
import com.hackanet.services.VKUniversityAPIServiceImpl;
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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/7/20
 */
@RestController
@Api(tags = "Position Controller")
@RequestMapping("/positions")
public class PositionController {

    private static final String ONE = "/{id}";
    private static final String BY_NAME = "/{name}";

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private VKUniversityAPIServiceImpl service;

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Add a new position")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PositionDto> add(@RequestParam @NotNull @NotEmpty String name,
                                           @AuthenticationPrincipal User user,
                                           HttpServletRequest request) {
        Position position = positionService.create(name);
        return new ResponseEntity<>(positionMapper.map(position), HttpStatus.CREATED);
    }

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get all positions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PositionDto>> getAll(@AuthenticationPrincipal User user,
                                                    HttpServletRequest request) {
        List<Position> all = positionService.getAll();
        return ResponseEntity.ok(positionMapper.map(all));
    }

    @GetMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get position info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PositionDto> get(@PathVariable Long id,
                                           @AuthenticationPrincipal User user,
                                           HttpServletRequest request) {
        Position position = positionService.get(id);
        return ResponseEntity.ok(positionMapper.map(position));
    }

    @PutMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Update position info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PositionDto> update(@PathVariable Long id,
                                              @RequestParam @NotNull @NotEmpty String name,
                                              @AuthenticationPrincipal User user,
                                              HttpServletRequest request) {
        Position update = positionService.update(id, name);
        return ResponseEntity.ok(positionMapper.map(update));
    }

    @GetMapping(BY_NAME)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get positions by name")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PositionDto>> getByName(@PathVariable String name,
                                                       @AuthenticationPrincipal User user,
                                                       HttpServletRequest request) {
        List<Position> positions = positionService.getByNameLike(name);
        return ResponseEntity.ok(positionMapper.map(positions));
    }
}
