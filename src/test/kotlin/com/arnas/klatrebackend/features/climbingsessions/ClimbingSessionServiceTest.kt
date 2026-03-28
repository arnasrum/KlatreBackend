package com.arnas.klatrebackend.features.climbingsessions

import com.arnas.klatrebackend.features.gradesystems.Grade
import com.arnas.klatrebackend.features.places.Place
import com.arnas.klatrebackend.features.routes.Route
import com.arnas.klatrebackend.features.gradesystems.GradeSystemRepository
import com.arnas.klatrebackend.features.places.PlaceRepository
import com.arnas.klatrebackend.features.routes.RouteRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.Optional
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class ClimbingSessionServiceTest {

    @Mock
    private lateinit var climbingSessionRepository: ClimbingSessionRepository

    @Mock
    private lateinit var placeRepository: PlaceRepository

    @Mock
    private lateinit var gradingSystemRepository: GradeSystemRepository

    @Mock
    private lateinit var routeRepository: RouteRepository

    @InjectMocks
    private lateinit var climbingSessionService: ClimbingSessionServiceDefault

    @Test
    fun testGetActiveSessionByGroup_Success() {
        val groupId = 1L
        val userId = 1L
        val expectedSession = ClimbingSession(1, userId, groupId, 1, 123456789, true, emptyList())

        `when`(climbingSessionRepository.getActiveSession(groupId, userId))
            .thenReturn(expectedSession)

        val result = climbingSessionService.getActiveSessionByGroup(groupId, userId)

        assertNotNull(result)
        assertEquals(expectedSession, result)
        verify(climbingSessionRepository).getActiveSession(groupId, userId)
    }

    @Test
    fun testGetActiveSessionByGroup_NoActiveSession() {
        val groupId = 1L
        val userId = 1L

        `when`(climbingSessionRepository.getActiveSession(groupId, userId))
            .thenReturn(null)

        val result = climbingSessionService.getActiveSessionByGroup(groupId, userId)

        assertNull(result)
        verify(climbingSessionRepository).getActiveSession(groupId, userId)
    }

    @Test
    fun testOpenSession_Success() {
        val groupId = 1L
        val placeId = 1L
        val userId = 1L
        val expectedSessionId = 123L
        val expectedSession = ClimbingSession(expectedSessionId, userId, groupId, placeId, 123456789, true, emptyList())

        `when`(climbingSessionRepository.openActiveSession(groupId, placeId, userId))
            .thenReturn(expectedSessionId)
        `when`(climbingSessionRepository.getClimbingSessionById(expectedSessionId))
            .thenReturn(expectedSession)

        val result = climbingSessionService.openSession(groupId, placeId, userId)

        assertNotNull(result)
        assertEquals(expectedSessionId, result.id)
        assertTrue(result.active)
        verify(climbingSessionRepository).openActiveSession(groupId, placeId, userId)
        verify(climbingSessionRepository).getClimbingSessionById(expectedSessionId)
    }

    @Test
    fun testCloseSession_WithSave() {
        val sessionId = 1L
        val userId = 1L

        val openSession = ClimbingSession(sessionId, userId, 1, 1, 123456789, true, emptyList())
        `when`(climbingSessionRepository.getClimbingSessionById(sessionId))
            .thenReturn(openSession)
        `when`(climbingSessionRepository.setSessionAsInactive(sessionId))
            .thenReturn(1)

        climbingSessionService.closeSession(sessionId, true, userId)

        verify(climbingSessionRepository).setSessionAsInactive(sessionId)
        verify(climbingSessionRepository, never()).deleteClimbingSession(sessionId)
    }

    @Test
    fun testCloseSession_WithoutSave() {
        val sessionId = 1L
        val userId = 1L

        val openSession = ClimbingSession(sessionId, userId, 1, 1, 123456789, true, emptyList())
        `when`(climbingSessionRepository.getClimbingSessionById(sessionId))
            .thenReturn(openSession)
        `when`(climbingSessionRepository.deleteClimbingSession(sessionId))
            .thenReturn(1)

        climbingSessionService.closeSession(sessionId, false, userId)

        verify(climbingSessionRepository, never()).setSessionAsInactive(sessionId)
        verify(climbingSessionRepository).deleteClimbingSession(sessionId)
    }

    @Test
    fun testAddRouteAttempt_Success() {
        val sessionId = 1L; val userId = 1L; val routeId = 301L; val groupId = 101L; val placeId = 201L; val gradingSystemId = 401L; val gradeId = 501L
        val openSession = ClimbingSession(sessionId, userId, placeId, groupId, 123456789, true, emptyList())
        val expectedAttemptId = 100L
        val place = Place(placeId, "routeName", null, groupId, gradingSystemId)
        val route = Route(routeId, "routeName", gradeId, placeId, null, false, null)
        val routeAttemptDTO = RouteAttemptDTO(5, true, routeId, 123456789, sessionId)
        val expectedAttempt = RouteAttempt(expectedAttemptId, 5, true, routeId, 123456789, sessionId)
        val expectedResult = RouteAttemptDisplay(expectedAttemptId, 5, true, "routeName", 123456789, "5a")
        val grades = listOf(Grade(gradeId, "5a", 1))

        `when`(gradingSystemRepository.getGradesBySystemId(gradingSystemId))
            .thenReturn(grades)
        `when`(routeRepository.getRouteById(anyLong()))
            .thenReturn(Optional.of(route))
        `when`(climbingSessionRepository.getClimbingSessionById(sessionId))
            .thenReturn(openSession)
        `when`(placeRepository.getPlaceById(anyLong()))
            .thenReturn(place)
        `when`(climbingSessionRepository.addRouteAttemptToActiveSession(sessionId, routeAttemptDTO))
            .thenReturn(expectedAttempt)

        val result = climbingSessionService.addRouteAttempt(sessionId, userId, routeAttemptDTO)

        assertNotNull(result)
        assertEquals(expectedResult, result)
        verify(climbingSessionRepository).addRouteAttemptToActiveSession(sessionId, routeAttemptDTO)
    }

    @Test
    fun testGetRouteAttempts_Success() {
        val sessionId = 1L
        val routeId1 = 301L
        val routeId2 = 302L
        val groupId = 101L
        val placeId = 201L
        val gradingSystemId = 401L
        val gradeId1 = 501L
        val gradeId2 = 502L

        val attempts = listOf(
            RouteAttempt(1, 3, true, routeId1, 123456789, sessionId),
            RouteAttempt(2, 5, false, routeId2, 123456790, sessionId)
        )
        val session = ClimbingSession(sessionId, 1L, placeId, groupId, 123456789, true, attempts)
        val place = Place(placeId, "routeName", null, groupId, gradingSystemId)
        val route1 = Route(routeId1, "routeName1", gradeId1, placeId, null, false, null)
        val route2 = Route(routeId2, "routeName2", gradeId2, placeId, null, false, null)
        val grade1 = Grade(gradeId1, "5a", 1)
        val grade2 = Grade(gradeId2, "9a", 2)
        val grades = listOf(grade1, grade2)

        val expectedResult = listOf(
            RouteAttemptDisplay(1, 3, true, "routeName1", 123456789, "5a"),
            RouteAttemptDisplay(2, 5, false, "routeName2", 123456790, "9a")
        )

        `when`(climbingSessionRepository.getClimbingSessionById(anyLong()))
            .thenReturn(session)
        `when`(placeRepository.getPlaceById(placeId))
            .thenReturn(place)
        `when`(routeRepository.getRouteById(routeId1))
            .thenReturn(Optional.of(route1))
        `when`(routeRepository.getRouteById(routeId2))
            .thenReturn(Optional.of(route2))
        `when`(climbingSessionRepository.getRouteAttemptsBySessionId(sessionId))
            .thenReturn(attempts)
        `when`(gradingSystemRepository.getGradesBySystemId(gradingSystemId))
            .thenReturn(grades)

        val result = climbingSessionService.getRouteAttempts(sessionId)

        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(expectedResult.first(), result.first())
        assertEquals(expectedResult.last(), result.last())
    }

    @Test
    fun testGetRouteAttempts_EmptyList() {
        val sessionId = 1L
        val placeId = 201L
        val groupId = 101L

        val openSession = ClimbingSession(sessionId, 1L, placeId, groupId, 123456789, true, emptyList())

        `when`(climbingSessionRepository.getClimbingSessionById(sessionId))
            .thenReturn(openSession)

        val result = climbingSessionService.getRouteAttempts(sessionId)

        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testGetPastSessionsByGroup_Success() {
        val groupId = 1L
        val userId = 1L
        val pastSessions = listOf(
            ClimbingSession(1, userId, groupId, 1, 123456789, false, emptyList()),
            ClimbingSession(2, userId, groupId, 1, 123456790, false, emptyList())
        )

        `when`(climbingSessionRepository.getPastSessions(groupId, userId))
            .thenReturn(pastSessions)

        val result = climbingSessionService.getPastSessionsByGroup(groupId, userId)

        assertNotNull(result)
        assertEquals(2, result.size)
        verify(climbingSessionRepository).getPastSessions(groupId, userId)
    }

    @Test
    fun testGetPastSessionsByGroup_EmptyList() {
        val groupId = 1L
        val userId = 1L

        `when`(climbingSessionRepository.getPastSessions(groupId, userId))
            .thenReturn(emptyList())

        val result = climbingSessionService.getPastSessionsByGroup(groupId, userId)

        assertNotNull(result)
        assertTrue(result.isEmpty())
        verify(climbingSessionRepository).getPastSessions(groupId, userId)
    }
}

