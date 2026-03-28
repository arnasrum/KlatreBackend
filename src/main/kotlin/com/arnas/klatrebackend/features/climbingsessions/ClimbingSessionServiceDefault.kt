package com.arnas.klatrebackend.features.climbingsessions

import com.arnas.klatrebackend.features.auth.RequireGroupAccess
import com.arnas.klatrebackend.features.auth.GroupAccessSource
import com.arnas.klatrebackend.features.gradesystems.Grade
import com.arnas.klatrebackend.features.gradesystems.GradeSystemRepositoryDefault
import com.arnas.klatrebackend.features.places.PlaceRepository
import com.arnas.klatrebackend.features.routes.RouteRepository
import org.springframework.stereotype.Service

@Service
class ClimbingSessionServiceDefault(
    private val climbingSessionRepository: ClimbingSessionRepository,
    private val placeRepository: PlaceRepository,
    private val gradingSystemRepository: GradeSystemRepositoryDefault,
    private val routeRepository: RouteRepository
) : ClimbingSessionService {

    @RequireGroupAccess
    override fun getActiveSessionByGroup(groupId: Long, userId: Long): ClimbingSession? {
        return climbingSessionRepository.getActiveSession(groupId, userId)
    }

    @RequireGroupAccess
    override fun getPastSessionsByGroup(groupId: Long, userId: Long): List<ClimbingSession> {
        return climbingSessionRepository.getPastSessions(groupId, userId)
    }

    @RequireGroupAccess
    override fun uploadSession(userId: Long, climbingSession: ClimbingSessionDTO) {
        throw RuntimeException("Not yet implemented")
    }

    @RequireGroupAccess
    override fun openSession(groupId: Long, placeId: Long, userId: Long): ClimbingSession {
        val activeSessionId = climbingSessionRepository.openActiveSession(userId, groupId, placeId)
        return climbingSessionRepository.getClimbingSessionById(activeSessionId)
            ?: throw RuntimeException("Session not found")
    }

    @RequireGroupAccess(resolveGroupFrom = GroupAccessSource.FROM_SESSION)
    override fun closeSession(sessionId: Long, save: Boolean, userId: Long) {
        val session = climbingSessionRepository.getClimbingSessionById(sessionId)
        if (session == null || !session.active) {
            throw RuntimeException("Session not found or not active")
        }
        val rowsAffected = if (save) {
            climbingSessionRepository.setSessionAsInactive(session.id)
        } else {
            climbingSessionRepository.deleteClimbingSession(session.id)
        }
        if (rowsAffected != 1) throw RuntimeException("Only one row should be affected")
    }

    override fun addRouteAttempt(activeSessionId: Long, userId: Long, routeAttempt: RouteAttemptDTO): RouteAttemptDisplay {
        val session = climbingSessionRepository.getClimbingSessionById(activeSessionId)
            ?: throw RuntimeException("Session not found")
        if (session.userId != userId) throw RuntimeException("User is not the owner of the session")
        if (!session.active) throw RuntimeException("Session is not active")
        val insertedRouteAttempt = climbingSessionRepository.addRouteAttemptToActiveSession(activeSessionId, routeAttempt)
        return try {
            routeAttemptToDisplay(insertedRouteAttempt)
        } catch (e: Exception) {
            throw RuntimeException("Error while converting route attempt to display object")
        }
    }

    override fun getRouteAttemptById(attemptId: Long): RouteAttempt {
        return climbingSessionRepository.getRouteAttemptById(attemptId)
    }

    @RequireGroupAccess(resolveGroupFrom = GroupAccessSource.FROM_SESSION)
    override fun removeRouteAttempt(attemptId: Long, sessionId: Long, userId: Long) {
        val rowsAffected = climbingSessionRepository.deleteRouteAttempt(attemptId)
        if (rowsAffected != 1) throw RuntimeException("Only one row should be affected")
    }

    override fun getRouteAttempts(sessionId: Long): List<RouteAttemptDisplay> {
        val routeAttempts = climbingSessionRepository.getRouteAttemptsBySessionId(sessionId)
        return routeAttempts.map { routeAttempt ->
            try {
                routeAttemptToDisplay(routeAttempt)
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException("Error while converting route attempt to display object")
            }
        }
    }

    override fun updateRouteAttempt(userId: Long, routeAttempt: RouteAttempt) {
        val rowsAffected = climbingSessionRepository.updateRouteAttempt(routeAttempt)
        if (rowsAffected != 1) throw RuntimeException("Only one row should be affected")
    }

    override fun getPastSessions(groupId: Long, userId: Long): List<ClimbingSessionDisplay> {
        val pastSessions = climbingSessionRepository.getPastSessions(groupId, userId)
        return pastSessions.map { session ->
            val routeDisplays = session.routeAttempts.map { attempt ->
                try {
                    routeAttemptToDisplay(attempt)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
            ClimbingSessionDisplay(
                id = session.id,
                userId = session.userId,
                placeId = session.placeId,
                groupId = session.groupId,
                timestamp = session.timestamp,
                active = session.active,
                routeAttempts = routeDisplays
            )
        }
    }

    @Throws(Exception::class)
    private fun routeAttemptToDisplay(routeAttempt: RouteAttempt): RouteAttemptDisplay {
        val session = climbingSessionRepository.getClimbingSessionById(routeAttempt.session)
            ?: throw Exception("Session has not been opened yet")
        val place = placeRepository.getPlaceById(session.placeId)
            ?: throw Exception("Session has not a valid place")
        val grades = gradingSystemRepository.getGradesBySystemId(place.gradingSystemId)
        val routeOpt = routeRepository.getRouteById(routeAttempt.routeId)
        if (routeOpt.isEmpty) throw Exception("Route not found")
        val route = routeOpt.get()
        val gradeObject = grades.firstOrNull { grade: Grade -> grade.id == route.gradeId }
            ?: throw Exception("Grade not found")
        return RouteAttemptDisplay(
            id = routeAttempt.id,
            attempts = routeAttempt.attempts,
            completed = routeAttempt.completed,
            routeName = route.name,
            timestamp = routeAttempt.timestamp,
            gradeName = gradeObject.gradeString
        )
    }
}

