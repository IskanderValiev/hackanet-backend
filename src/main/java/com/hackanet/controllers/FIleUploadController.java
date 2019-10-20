package com.hackanet.controllers;

import com.hackanet.json.dto.FileInfoDto;
import com.hackanet.json.mappers.FileInfoMapper;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.FileInfo;
import com.hackanet.models.User;
import com.hackanet.services.UploadService;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@RestController
@RequestMapping("/files")
@Api(tags = "File Upload Controller")
public class FIleUploadController {

    private static final String UPLOAD = "/upload";

    @Autowired
    private UploadService uploadService;

    @Autowired
    @Qualifier("fileInfoMapper")
    private Mapper<FileInfo, FileInfoDto> mapper;

    @PostMapping(UPLOAD)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Upload file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileInfoDto> upload(@RequestParam("file") MultipartFile file,
                                              @AuthenticationPrincipal User user) {
        FileInfo fileInfo = uploadService.uploadFile(user, file);
        return new ResponseEntity<>(mapper.map(fileInfo), HttpStatus.OK);
    }
}
