package com.arnas.klatrebackend.interfaces.repositories;

import com.arnas.klatrebackend.records.ClimbingSession;
import com.arnas.klatrebackend.records.ClimbingSessionDTO;
import com.arnas.klatrebackend.records.RouteAttempt;
import com.arnas.klatrebackend.records.RouteAttemptDTO;

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
}
