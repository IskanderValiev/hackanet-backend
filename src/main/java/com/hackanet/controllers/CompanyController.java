package com.hackanet.controllers;

import com.hackanet.json.dto.CompanyDto;
import com.hackanet.json.dto.CompanyOwnerTokenDto;
import com.hackanet.json.forms.CompanyCreateForm;
import com.hackanet.json.forms.CompanySearchForm;
import com.hackanet.json.forms.CompanyUpdateForm;
import com.hackanet.json.mappers.CompanyMapper;
import com.hackanet.models.Company;
import com.hackanet.models.user.User;
import com.hackanet.services.CompanyService;
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

import javax.validation.Valid;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@RestController
@Api(tags = "Company Controller")
@RequestMapping("/companies")
public class CompanyController {

    private static final String REGISTER = "/register";
    private static final String COMPANY = "/{id}";
    private static final String UPDATE_STATUS = COMPANY + "/status";
    private static final String ADD_BY_NAME = "/add";

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyMapper companyMapper;

    @PostMapping(REGISTER)
    @ApiOperation("Register a new company")
    public ResponseEntity<CompanyOwnerTokenDto> register(@Valid @RequestBody CompanyCreateForm form) {
        CompanyOwnerTokenDto companyOwnerTokenDto = companyService.registerCompany(form);
        return new ResponseEntity<>(companyOwnerTokenDto, HttpStatus.CREATED);
    }

    @GetMapping(COMPANY)
    @ApiOperation("Get information about the company")
    public ResponseEntity<CompanyDto> get(@PathVariable Long id) {
        Company company = companyService.get(id);
        return ResponseEntity.ok(companyMapper.map(company));
    }

    @PutMapping(COMPANY)
    @ApiOperation("Update the information about the company")
    public ResponseEntity<CompanyDto> update(@PathVariable Long id,
                                             @RequestBody CompanyUpdateForm form,
                                             @AuthenticationPrincipal User user) {
        Company company = companyService.update(id, user, form);
        return ResponseEntity.ok(companyMapper.map(company));
    }

    @GetMapping(UPDATE_STATUS)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Update status of the company")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<String> updateStatus(@PathVariable Long id,
                                               @RequestParam Boolean approved) {
        companyService.updateApprovedStatus(id, approved);
        return ResponseEntity.ok("OK");
    }

    @PostMapping
    @ApiOperation("Search companies")
    public ResponseEntity<List<CompanyDto>> search(@RequestBody CompanySearchForm form) {
        List<Company> companies = companyService.getCompaniesList(form);
        return ResponseEntity.ok(companyMapper.map(companies));
    }

    @GetMapping(ADD_BY_NAME)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Add by name")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompanyDto> addByName(@RequestParam String name, @RequestParam String country, @RequestParam String city) {
        Company company = companyService.createCompanyByName(name, country, city);
        return ResponseEntity.ok(companyMapper.map(company));
    }
}
