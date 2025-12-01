package com.arnas.klatrebackend.features.climbingsessions;

import com.arnas.klatrebackend.features.gradesystems.Grade;
import com.arnas.klatrebackend.features.places.Place;
import com.arnas.klatrebackend.features.routes.Route;
import com.arnas.klatrebackend.features.gradesystems.GradeSystemRepository;
import com.arnas.klatrebackend.features.places.PlaceRepository;
import com.arnas.klatrebackend.features.routes.RouteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ClimbingSessionServiceTest {

    @Mock
    private ClimbingSessionRepository climbingSessionRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private GradeSystemRepository gradingSystemRepository;
    @Mock
    private RouteRepository routeRepository;
    @InjectMocks
    private ClimbingSessionServiceDefault climbingSessionService;

    @Test
    void testGetActiveSessionByGroup_Success() {
        long groupId = 1L;
        long userId = 1L;
        ClimbingSession expectedSession = new ClimbingSession(1, userId, groupId, 1, 123456789, true, List.of());

        when(climbingSessionRepository.getActiveSession(groupId, userId))
            .thenReturn(expectedSession);

        ClimbingSession result = climbingSessionService.getActiveSessionByGroup(groupId, userId);

        assertNotNull(result);
        assertEquals(expectedSession, result);
        verify(climbingSessionRepository).getActiveSession(groupId, userId);
    }

    @Test
    void testGetActiveSessionByGroup_NoActiveSession() {
        long groupId = 1L;
        long userId = 1L;

        when(climbingSessionRepository.getActiveSession(groupId, userId))
            .thenReturn(null);

        ClimbingSession result = climbingSessionService.getActiveSessionByGroup(groupId, userId);

        assertNull(result);
        verify(climbingSessionRepository).getActiveSession(groupId, userId);
    }

    @Test
    void testOpenSession_Success() {
        long groupId = 1L;
        long placeId = 1L;
        long userId = 1L;
        long expectedSessionId = 123L;
        ClimbingSession expectedSession = new ClimbingSession(expectedSessionId, userId, groupId, placeId, 123456789, true, List.of());

        when(climbingSessionRepository.openActiveSession(groupId, placeId, userId))
            .thenReturn(expectedSessionId);
        when(climbingSessionRepository.getClimbingSessionById(expectedSessionId))
            .thenReturn(expectedSession);

        ClimbingSession result = climbingSessionService.openSession(groupId, placeId, userId);

        assertNotNull(result);
        assertEquals(expectedSessionId, result.id());
        assertTrue(result.active());
        verify(climbingSessionRepository).openActiveSession(groupId, placeId, userId);
        verify(climbingSessionRepository).getClimbingSessionById(expectedSessionId);
    }

    @Test
    void testCloseSession_WithSave() {
        long sessionId = 1L;
        long userId = 1L;

        var openSession = new ClimbingSession(sessionId, userId, 1, 1, 123456789, true, List.of());
        when(climbingSessionRepository.getClimbingSessionById(sessionId))
                .thenReturn(openSession);
        when(climbingSessionRepository.setSessionAsInactive(sessionId))
            .thenReturn(1);

        climbingSessionService.closeSession(sessionId, true, userId);

        verify(climbingSessionRepository).setSessionAsInactive(sessionId);
        verify(climbingSessionRepository, never()).deleteClimbingSession(sessionId);
    }

    @Test
    void testCloseSession_WithoutSave() {
        long sessionId = 1L;
        long userId = 1L;

        var openSession = new ClimbingSession(sessionId, userId, 1, 1, 123456789, true, List.of());
        when(climbingSessionRepository.getClimbingSessionById(sessionId))
                .thenReturn(openSession);
        when(climbingSessionRepository.deleteClimbingSession(sessionId))
            .thenReturn(1);

        climbingSessionService.closeSession(sessionId, false, userId);

        verify(climbingSessionRepository, never()).setSessionAsInactive(sessionId);
        verify(climbingSessionRepository).deleteClimbingSession(sessionId);
    }

    @Test
    void testAddRouteAttempt_Success() {
        long sessionId = 1L; long userId = 1L; long routeId = 301L; long groupId = 101L; long placeId = 201L; long gradingSystemId = 401L; long gradeId = 501;
        var openSession = new ClimbingSession(sessionId, userId, placeId, groupId, 123456789, true, List.of());
        long expectedAttemptId = 100L;
        var place = new Place(placeId, "routeName", null, groupId, gradingSystemId);
        var route = new Route(routeId, "routeName", gradeId, placeId, null, false, null);
        var routeAttemptDTO = new RouteAttemptDTO(5, true, routeId, 123456789, sessionId);
        var expectedAttempt = new RouteAttempt(expectedAttemptId, 5, true, routeId, 123456789, sessionId);
        var expectedResult = new RouteAttemptDisplay(expectedAttemptId, 5, true, "routeName", 123456789, "5a");
        var grades = List.of(new Grade(gradeId, "5a", 1));

        when(gradingSystemRepository.getGradesBySystemId(gradingSystemId))
            .thenReturn(grades);
        when(routeRepository.getRouteById(anyLong()))
            .thenReturn(route);
        when(climbingSessionRepository.getClimbingSessionById(sessionId))
            .thenReturn(openSession);
        when(placeRepository.getPlaceById(anyLong()))
            .thenReturn(place);
        when(climbingSessionRepository.addRouteAttemptToActiveSession(sessionId, routeAttemptDTO))
            .thenReturn(expectedAttempt);

        var result = climbingSessionService.addRouteAttempt(sessionId, userId, routeAttemptDTO);

        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(climbingSessionRepository).addRouteAttemptToActiveSession(sessionId, routeAttemptDTO);
    }

    @Test
    void testGetRouteAttempts_Success() {
        long sessionId = 1L;
        long routeId1 = 301L;
        long routeId2 = 302L;
        long groupId = 101L;
        long placeId = 201L;
        long gradingSystemId = 401L;
        long gradeId1 = 501;
        long gradeId2 = 502;

        List<RouteAttempt> attempts = List.of(
            new RouteAttempt(1, 3, true, routeId1, 123456789, sessionId),
            new RouteAttempt(2, 5, false, routeId2, 123456790, sessionId)
        );
        var session = new ClimbingSession(sessionId, 1L, placeId, groupId, 123456789, true, attempts);
        var place = new Place(placeId, "routeName", null, groupId, gradingSystemId);
        Route route1 = new Route(routeId1, "routeName1", gradeId1, placeId, null, false, null);
        Route route2 = new Route(routeId2, "routeName2", gradeId2, placeId, null, false, null);
        Grade grade1 = new Grade(gradeId1, "5a", 1);
        Grade grade2 = new Grade(gradeId2, "9a", 2);
        List<Grade> grades = List.of(grade1, grade2);

        var expectedResult = List.of(
            new RouteAttemptDisplay(1, 3, true, "routeName1", 123456789, "5a"),
            new RouteAttemptDisplay(2, 5, false, "routeName2", 123456790, "9a")
        );

        when(climbingSessionRepository.getClimbingSessionById(anyLong()))
                .thenReturn(session);
        when(placeRepository.getPlaceById(placeId))
                .thenReturn(place);
        when(routeRepository.getRouteById(routeId1))
                .thenReturn(route1);
        when(routeRepository.getRouteById(routeId2))
                .thenReturn(route2);
        when(climbingSessionRepository.getRouteAttemptsBySessionId(sessionId))
                .thenReturn(attempts);
        when(gradingSystemRepository.getGradesBySystemId(gradingSystemId))
            .thenReturn(grades);

        var result = climbingSessionService.getRouteAttempts(sessionId);


        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResult.getFirst(), result.getFirst());
        assertEquals(expectedResult.getLast(), result.getLast());
    }

    @Test
    void testGetRouteAttempts_EmptyList() {
        long sessionId = 1L;
        long placeId = 201L;
        long groupId = 101L;

        var openSession = new ClimbingSession(sessionId, 1L, placeId, groupId, 123456789, true, List.of());

        when(climbingSessionRepository.getClimbingSessionById(sessionId))
            .thenReturn(openSession);

        var result = climbingSessionService.getRouteAttempts(sessionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        //verify(climbingSessionRepository).getClimbingSessionById(sessionId);
    }

    @Test
    void testGetPastSessionsByGroup_Success() {
        long groupId = 1L;
        long userId = 1L;
        List<ClimbingSession> pastSessions = List.of(
            new ClimbingSession(1, userId, groupId, 1, 123456789, false, List.of()),
            new ClimbingSession(2, userId, groupId, 1, 123456790, false, List.of())
        );

        when(climbingSessionRepository.getPastSessions(groupId, userId))
            .thenReturn(pastSessions);

        List<ClimbingSession> result = climbingSessionService.getPastSessionsByGroup(groupId, userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(climbingSessionRepository).getPastSessions(groupId, userId);
    }

    @Test
    void testGetPastSessionsByGroup_EmptyList() {
        long groupId = 1L;
        long userId = 1L;

        when(climbingSessionRepository.getPastSessions(groupId, userId))
            .thenReturn(List.of());

        List<ClimbingSession> result = climbingSessionService.getPastSessionsByGroup(groupId, userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(climbingSessionRepository).getPastSessions(groupId, userId);
    }
}
