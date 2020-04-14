package com.hackanet.controllers;

import com.hackanet.json.dto.ReportDto;
import com.hackanet.json.forms.ReportCreateFrom;
import com.hackanet.json.mappers.ReportMapper;
import com.hackanet.models.Report;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.ReportStatus;
import com.hackanet.services.ReportService;
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
 * on 3/11/20
 */
@RestController
@Api(tags = "Report Controller")
@RequestMapping("/reports")
public class ReportController {

    private static final String ONE = "/{id}";

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportMapper reportMapper;

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = false, dataType = "string", paramType = "header")
    })
    @ApiOperation("Report about a violation")
    public ResponseEntity<ReportDto> create(@Valid @RequestBody ReportCreateFrom form,
                                            @AuthenticationPrincipal User user) {
        Report report = reportService.report(user, form);
        return new ResponseEntity<>(reportMapper.map(report), HttpStatus.CREATED);
    }

    @PutMapping(ONE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Update the status of the report")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ReportDto> update(@PathVariable Long id,
                                            @RequestParam ReportStatus status) {
        Report report = reportService.updateStatus(id, status);
        return new ResponseEntity<>(reportMapper.map(report), HttpStatus.OK);
    }

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get unresolved reports")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<List<ReportDto>> getUnresolvedReports() {
        List<Report> report = reportService.getUnresolvedReports();
        return new ResponseEntity<>(reportMapper.map(report), HttpStatus.OK);
    }
}
