package com.arnas.klatrebackend.interfaces.services;

import com.arnas.klatrebackend.dataclasses.*;

import java.util.List;

public interface ClimbingSessionService {

    ClimbingSession getActiveSessionByGroup(long groupId, long userId);
    List<ClimbingSession> getPastSessionsByGroup(long groupId, long userId);
    void uploadSession(long userId, ClimbingSessionDTO climbingSession);
    ClimbingSession openSession(long groupId, long placeId, long userId);
    void closeSession(long sessionId, boolean save, long userId);
    RouteAttemptDisplay addRouteAttempt(long activeSessionId, long userId, RouteAttemptDTO routeAttempt);
    void removeRouteAttempt(long attemptId, long userId);
    RouteAttempt getRouteAttemptById(long attemptId);
    List<RouteAttemptDisplay> getRouteAttempts(long sessionId);
    void updateRouteAttempt(long userId, RouteAttempt routeAttempt);
    List<ClimbingSessionDisplay> getPastSessions(long groupId, long userId);

}