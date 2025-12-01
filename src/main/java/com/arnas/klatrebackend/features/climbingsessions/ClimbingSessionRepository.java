package com.arnas.klatrebackend.features.climbingsessions;

import org.springframework.lang.Nullable;

import java.util.List;

public interface ClimbingSessionRepository {
    @Nullable ClimbingSession getClimbingSessionById(long sessionId);
    @Nullable ClimbingSession getActiveSession(long groupId, long userId);
    List<ClimbingSession> getPastSessions(long groupId, long userId);
    long openActiveSession(long userId, long groupId, long placeId);
    int setSessionAsInactive(long activeSessionId);
    long uploadClimbingSession(ClimbingSessionDTO climbingSession);
    int deleteClimbingSession(long climbingSessionId);
    List<RouteAttempt> getRouteAttemptsBySessionId(long sessionId);
    int updateRouteAttempt(RouteAttempt routeAttempt);
    int deleteRouteAttempt(long routeAttemptId);
    RouteAttempt addRouteAttemptToActiveSession(long activeSessionId, RouteAttemptDTO routeAttempt);
    RouteAttempt getRouteAttemptById(long id);
}
