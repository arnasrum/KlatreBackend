package com.arnas.klatrebackend.features.climbingsessions;

import com.arnas.klatrebackend.annotation.RequireGroupAccess;
import com.arnas.klatrebackend.features.auth.GroupAccessSource;
import com.arnas.klatrebackend.features.gradesystems.Grade;
import com.arnas.klatrebackend.features.gradesystems.GradeSystemRepository;
import com.arnas.klatrebackend.features.places.PlaceRepositoryInterface;
import com.arnas.klatrebackend.features.routes.RouteRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ClimbingSessionServiceDefault implements ClimbingSessionService {

    final private ClimbingSessionRepository climbingSessionRepository;
    final private PlaceRepositoryInterface placeRepository;
    final private GradeSystemRepository gradingSystemRepository;
    final private RouteRepository routeRepository;

    public ClimbingSessionServiceDefault(
            @Autowired ClimbingSessionRepository climbingSessionRepository,
            @Autowired PlaceRepositoryInterface placeRepository,
            @Autowired GradeSystemRepository gradingSystemRepository,
            @Autowired RouteRepository routeRepository) {
        this.climbingSessionRepository = climbingSessionRepository;
        this.placeRepository = placeRepository;
        this.gradingSystemRepository = gradingSystemRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    @RequireGroupAccess
    public ClimbingSession getActiveSessionByGroup(long groupId, long userId) {
        return climbingSessionRepository.getActiveSession(groupId, userId);
    }

    @Override
    @RequireGroupAccess
    public List<ClimbingSession> getPastSessionsByGroup(long groupId, long userId) {
        return climbingSessionRepository.getPastSessions(groupId, userId);
    }


    @Override
    @RequireGroupAccess
    public void uploadSession(long userId, @NotNull ClimbingSessionDTO climbingSession) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    @RequireGroupAccess
    @NotNull
    public ClimbingSession openSession(long groupId, long placeId, long userId) {
        var activeSessionId = climbingSessionRepository.openActiveSession(userId, groupId, placeId);
        var session = climbingSessionRepository.getClimbingSessionById(activeSessionId);
        if(session == null) throw new RuntimeException("Session not found");
        return session;
    }

    @Override
    @RequireGroupAccess(resolveGroupFrom = GroupAccessSource.FROM_SESSION)
    public void closeSession(long sessionId, boolean save, long userId) {
        var session = climbingSessionRepository.getClimbingSessionById(sessionId);
        if(session == null || !session.active()) {
            throw new RuntimeException("Session not found or not active");
        }
        int rowsAffected;
        if(save) {
            rowsAffected = climbingSessionRepository.setSessionAsInactive(session.id());
        } else {
            rowsAffected = climbingSessionRepository.deleteClimbingSession(session.id());
        }
        if(rowsAffected != 1) throw new RuntimeException("Only one row should be affected");
    }

    @Override
    @NotNull
    public RouteAttemptDisplay addRouteAttempt(long activeSessionId, long userId, @NotNull RouteAttemptDTO routeAttempt) {
        var session = climbingSessionRepository.getClimbingSessionById(activeSessionId);
        if(session == null) throw new RuntimeException("Session not found");
        if(session.userId() != userId) throw new RuntimeException("User is not the owner of the session");
        if(!session.active()) throw new RuntimeException("Session is not active");
        var insertedRouteAttempt = climbingSessionRepository.addRouteAttemptToActiveSession(activeSessionId, routeAttempt);
        RouteAttemptDisplay display;
        try {
            display = routeAttemptToDisplay(insertedRouteAttempt);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting route attempt to display object");
        }
        return display;
    }

    @Override
    public RouteAttempt getRouteAttemptById(long attemptId) {
        return climbingSessionRepository.getRouteAttemptById(attemptId);
    }

    @Override
    @RequireGroupAccess(resolveGroupFrom = GroupAccessSource.FROM_SESSION)
    public void removeRouteAttempt(long attemptId, long sessionId, long userId) {
        var rowsAffected = climbingSessionRepository.deleteRouteAttempt(attemptId);
        if(rowsAffected != 1) throw new RuntimeException("Only one row should be affected");
    }

    @Override
    public List<RouteAttemptDisplay> getRouteAttempts(long sessionId) {
        var routeAttempts = climbingSessionRepository.getRouteAttemptsBySessionId(sessionId);
        return routeAttempts.stream().map((routeAttempt) -> {
            try {
                return routeAttemptToDisplay(routeAttempt);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error while converting route attempt to display object");
            }
        }).toList();
    }

    @Override
    public void updateRouteAttempt(long userId, RouteAttempt routeAttempt) {
        var rowsAffected = climbingSessionRepository.updateRouteAttempt(routeAttempt);
        if(rowsAffected != 1) throw new RuntimeException("Only one row should be affected");
    }


    @Override
    public List<ClimbingSessionDisplay> getPastSessions(long groupId, long userId) {
        var pastSessions = climbingSessionRepository.getPastSessions(groupId, userId);
        return pastSessions.stream().map( session -> {
            Stream<RouteAttemptDisplay> routeDisplays = session.routeAttempts().stream().map(attempt -> {
                try {
                    return routeAttemptToDisplay(attempt);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return new ClimbingSessionDisplay(
                    session.id(),
                    session.userId(),
                    session.placeId(),
                    session.groupId(),
                    session.timestamp(),
                    session.active(),
                    routeDisplays.toList()
            );
        }).toList();
    }

    private RouteAttemptDisplay routeAttemptToDisplay(RouteAttempt routeAttempt) throws Exception {
        var session = climbingSessionRepository.getClimbingSessionById(routeAttempt.session());
        if(session == null) throw new Exception("Session has not been opened yet");
        var place = placeRepository.getPlaceById(session.placeId());
        if(place == null) throw new Exception("Session has not a valid place");
        var grades = gradingSystemRepository.getGradesBySystemId(place.getGradingSystemId());
        var routeOpt = routeRepository.getRouteById(routeAttempt.routeId());
        if(routeOpt.isEmpty()) throw new Exception("Route not found");
        var route = routeOpt.get();
        var gradeObject = grades.stream().filter((Grade grade) -> grade.getId() == route.getGradeId()).findFirst().orElse(null);
        if(gradeObject == null) throw new Exception("Grade not found");
        return new RouteAttemptDisplay(routeAttempt.id(), routeAttempt.attempts(), routeAttempt.completed(), route.getName(),  routeAttempt.timestamp(), gradeObject.getGradeString());
    }
}
