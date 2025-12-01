package com.arnas.klatrebackend.features.stats;

import com.arnas.klatrebackend.features.users.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @RequestMapping("/user")
    public ResponseEntity<List<UserGroupSessionStats>> getStats(@RequestParam long groupId, User user ){
        var userSessionStats = statsService.getUserAttemptActivity(user.getId(), groupId);
        return ResponseEntity.ok().body(userSessionStats);
    }
}
