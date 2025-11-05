package com.arnas.klatrebackend.controllers;

import com.arnas.klatrebackend.dataclasses.User;
import com.arnas.klatrebackend.dataclasses.UserGroupSessionStats;
import com.arnas.klatrebackend.interfaces.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsControllerDefault {

    private final StatsService statsService;
    public StatsControllerDefault(StatsService statsService) {
        this.statsService = statsService;
    }

    @RequestMapping("/user")
    public ResponseEntity<List<UserGroupSessionStats>> getStats(@RequestParam long groupId, User user ){
        var userSessionStats = statsService.getUserAttemptActivity(user.getId(), groupId);
        return ResponseEntity.ok().body(userSessionStats);
    }
}
