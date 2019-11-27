package com.hackanet.controllers;

import com.hackanet.json.dto.PartnerDto;
import com.hackanet.json.forms.PartnerCreateForm;
import com.hackanet.json.mappers.PartnerMapper;
import com.hackanet.models.Partner;
import com.hackanet.services.PartnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/27/19
 */
@RestController
@RequestMapping("/partners")
@Api(tags = "Partner Controller")
public class PartnerController {

    private static final String CREATE = "/create";
    private static final String ONE_PARTNER = "/{id}";

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PartnerMapper partnerMapper;

    @PostMapping(CREATE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Create a new partner")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<PartnerDto> create(@Valid @RequestBody PartnerCreateForm form) {
        Partner partner = partnerService.create(form);
        return new ResponseEntity<>(partnerMapper.map(partner), HttpStatus.CREATED);
    }

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Create a new partner")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<PartnerDto>> getAll() {
        List<Partner> partners = partnerService.getAll();
        return ResponseEntity.ok(partnerMapper.map(partners));
    }

    @DeleteMapping(ONE_PARTNER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Create a new partner")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        partnerService.delete(id);
        return ResponseEntity.ok("OK");
    }
}
