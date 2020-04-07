package com.hackanet.controllers;

import com.hackanet.json.dto.VkUniversityResponse;
import com.hackanet.models.User;
import com.hackanet.services.UniversityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/7/20
 */
@RequestMapping("/universities")
@Api(tags = "University Controller")
@RestController
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Search university")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VkUniversityResponse.VkUniversityListDto> search(@RequestParam String query,
                                                                           @AuthenticationPrincipal User user,
                                                                           HttpServletRequest request) {
        return ResponseEntity.ok(universityService.getUniversity(query));
    }
}
