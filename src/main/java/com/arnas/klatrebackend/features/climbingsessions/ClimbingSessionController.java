package com.arnas.klatrebackend.features.climbingsessions;

import com.arnas.klatrebackend.features.users.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/climbingSessions")
public class ClimbingSessionController {
    private final ClimbingSessionService climbingSessionService;

    public ClimbingSessionController(ClimbingSessionService climbingSessionService) {
        this.climbingSessionService = climbingSessionService;
    }

    @PostMapping("")
    ResponseEntity<String> uploadSession(@RequestBody ClimbingSessionDTO climbingSession, User user) {
        climbingSessionService.uploadSession(user.getId(), climbingSession);
        return ResponseEntity.ok("Session uploaded successfully");
    }

    @GetMapping("/active")
    ResponseEntity<ClimbingSession> getActiveSession(@RequestParam long groupId, User user) {
        var activeSession = climbingSessionService.getActiveSessionByGroup(groupId, user.getId());
        return ResponseEntity.ok().body(activeSession);
    }

    @PostMapping("/open")
    ResponseEntity<ClimbingSession> openPersonalSession(@RequestParam long groupId, @RequestParam long placeId, User user)  {
        var openedSession = climbingSessionService.openSession(groupId, placeId, user.getId());
        return ResponseEntity.ok().body(openedSession);
    }

    @PutMapping("/close")
    ResponseEntity<String> closePersonalSession(@RequestParam long sessionId, @RequestParam boolean save, User user)  {
        climbingSessionService.closeSession(sessionId, save, user.getId());
        return ResponseEntity.ok().body("Session was closed successfully");
    }

    @GetMapping("/attempts")
    ResponseEntity<List<RouteAttemptDisplay>> getSessionAttempts(@RequestParam long sessionId, User user) {
        var routeAttempts = climbingSessionService.getRouteAttempts(sessionId);
        return ResponseEntity.ok().body(routeAttempts);
    }

    @PostMapping("/add/attempt")
    ResponseEntity<String> addAttempt(@RequestParam long sessionId, @RequestBody RouteAttemptDTO routeAttempt, User user)  {
        climbingSessionService.addRouteAttempt(sessionId, user.getId(), routeAttempt);
        return ResponseEntity.ok("Attempt added successfully");
    }

    @PutMapping("/update/attempt")
    ResponseEntity<String> updateAttempt(@RequestBody RouteAttempt newRouteAttempt, User user) {
        climbingSessionService.updateRouteAttempt(user.getId(), newRouteAttempt);
        return ResponseEntity.ok("updated successfully");
    }

    @DeleteMapping("/remove/attempt/{attemptId}")
    ResponseEntity<String> addAttempt(@PathVariable long attemptId, User user) {
        //val routeAttemptId =  requestBody["attemptId"]?.toLong() ?: return ResponseEntity.badRequest().body(mapOf("message" to "Route attempt id is required"))
        var attempt = climbingSessionService.getRouteAttemptById(attemptId);
        climbingSessionService.removeRouteAttempt(attemptId, attempt.session(), user.getId());
        return ResponseEntity.ok("Attempt removed successfully");
    }

    @GetMapping("/past/{groupId}")
    ResponseEntity<List<ClimbingSessionDisplay>> getPastSessions(@PathVariable long groupId, User user) {
        var serviceResult = climbingSessionService.getPastSessions(groupId, user.getId());
        return ResponseEntity.ok().body(serviceResult);
    }
}