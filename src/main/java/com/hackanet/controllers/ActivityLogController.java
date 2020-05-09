package com.hackanet.controllers;

import com.google.common.collect.Lists;
import com.hackanet.models.chat.Message;
import com.hackanet.models.log.ActivityLog;
import com.hackanet.repositories.chat.MessageRepository;
import com.hackanet.repositories.log.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public ResponseEntity<List<ActivityLog>> get() {
        ArrayList<ActivityLog> logs = Lists.newArrayList(repository.findAll());
        List<ActivityLog> sortedLogs = logs.stream().sorted(Comparator.comparing(ActivityLog::getDate).reversed()).collect(Collectors.toList());
        return ResponseEntity.ok(sortedLogs);
    }

    @DeleteMapping
    public ResponseEntity<String> delete() {
        repository.deleteAll();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    public List<Message> getMessages() {
        return Lists.newArrayList(messageRepository.findAll());
    }

    @DeleteMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    public List<Message> deleteMessages() {
        messageRepository.deleteAll();
        return Lists.newArrayList(messageRepository.findAll());
    }
}
