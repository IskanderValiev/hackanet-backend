package com.hackanet.controllers;

import com.hackanet.json.dto.PortfolioDto;
import com.hackanet.json.mappers.PortfolioMapper;
import com.hackanet.models.user.Portfolio;
import com.hackanet.services.PortfolioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@RestController
@Api(tags = "Portfolio Controller")
@RequestMapping("/portfolio")
public class PortfolioController {

    private static final String BY_USER = "/user/{id}";

    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private PortfolioMapper portfolioMapper;

    @GetMapping(BY_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get portfolio of the user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PortfolioDto> getByUser(@PathVariable Long id) {
        Portfolio portfolio = portfolioService.getByUserId(id);
        return ResponseEntity.ok(portfolioMapper.map(portfolio));
    }
}
