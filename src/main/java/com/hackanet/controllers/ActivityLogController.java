package com.hackanet.controllers;

import com.google.common.collect.Lists;
import com.hackanet.models.log.ActivityLog;
import com.hackanet.repositories.log.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/18/20
 */
@RestController
@RequestMapping("/superadmin/activity/logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogRepository repository;

    @GetMapping
    public ResponseEntity<List<ActivityLog>> get() {
        return ResponseEntity.ok(Lists.newArrayList(repository.findAll()));
    }

    @DeleteMapping
    public ResponseEntity<String> delete() {
        repository.deleteAll();
        return ResponseEntity.ok("OK");
    }
}
