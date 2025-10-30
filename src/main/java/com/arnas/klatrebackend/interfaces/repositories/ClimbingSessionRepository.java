package com.arnas.klatrebackend.interfaces.repositories;

import com.arnas.KlatreBackend.records.ClimbingSession;
import com.arnas.KlatreBackend.records.ClimbingSessionDTO;
import com.arnas.KlatreBackend.records.RouteAttempt;
import com.arnas.KlatreBackend.records.RouteAttemptDTO;

import java.util.Optional;

public interface ClimbingSessionRepository {
    Optional<ClimbingSession> getClimbingSessionById(long sessionId);
    Optional<ClimbingSession> getActiveSession(long groupId, long userId);
    Optional<ClimbingSession> getActiveSession(long activeSessionId);
    ClimbingSession[] getPastSessions(long groupId, long userId);
    long openActiveSession(long userId, long groupId, long placeId);
    int setSessionAsInactive(long activeSessionId);
    long uploadClimbingSession(ClimbingSessionDTO climbingSession);
    int deleteClimbingSession(long climbingSessionId);
    RouteAttempt[] getRouteAttemptsBySessionId(long sessionId);
    int updateRouteAttempt(RouteAttempt routeAttempt);
    int deleteRouteAttempt(long routeAttemptId);
    RouteAttempt addRouteAttemptToActiveSession(long activeSessionId, RouteAttemptDTO routeAttempt);
}
