package com.hackanet.controllers;

import com.hackanet.json.dto.SkillDto;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.Skill;
import com.hackanet.services.SkillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@RestController
@RequestMapping("/skills")
@Api(tags = "Skill Controller")
public class SkillController {

    private static final String ADD = "/add";
    private static final String SKILL = "/{id}";
    private static final String LIST_OF_SKILLS = "/list";

    @Autowired
    @Qualifier("skillMapper")
    private Mapper<Skill, SkillDto> mapper;

    @Autowired
    private SkillService skillService;

    @PostMapping(ADD)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Add new skill")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SkillDto> add(@RequestParam String name) {
        Skill skill = skillService.add(name);
        return new ResponseEntity<>(mapper.map(skill), HttpStatus.CREATED);
    }

    @GetMapping
    @ApiOperation(value = "Get all skills")
    public ResponseEntity<List<SkillDto>> getAll() {
        List<Skill> skills = skillService.getAll();
        return ResponseEntity.ok(mapper.map(skills));
    }

    @GetMapping(SKILL)
    @ApiOperation(value = "Get information about the skill")
    public ResponseEntity<SkillDto> get(@PathVariable Long id) {
        Skill skill = skillService.get(id);
        return ResponseEntity.ok(mapper.map(skill));
    }

    @PutMapping(SKILL)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Update information of the skill")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SkillDto> update(@PathVariable Long id,
                                           @RequestParam String name) {
        Skill skill = skillService.update(id, name);
        return ResponseEntity.ok(mapper.map(skill));
    }

    @DeleteMapping(SKILL)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Delete information of the skill")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(LIST_OF_SKILLS)
    @ApiOperation(value = "Get information about skill by name like")
    public ResponseEntity<List<SkillDto>> getByNameLike(@RequestParam String name) {
        List<Skill> skills = skillService.getByNameLike(name);
        return ResponseEntity.ok(mapper.map(skills));
    }
}
