package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.ActiveSession
import com.arnas.klatrebackend.dataclasses.ClimbingSession
import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.RouteAttempt
import com.arnas.klatrebackend.dataclasses.RouteAttemptDTO
import com.arnas.klatrebackend.dataclasses.RouteAttemptDisplay
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.dataclasses.UpdateAttemptRequest
import com.arnas.klatrebackend.interfaces.repositories.GradingSystemRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import com.arnas.klatrebackend.repositories.BoulderRepository
import com.arnas.klatrebackend.repositories.ClimbingSessionRepository
import org.springframework.stereotype.Service

@Service
class ClimbingSessionService(
    private val climbingSessionRepository: ClimbingSessionRepository,
    private val placeRepository: PlaceRepositoryInterface,
    private val gradingSystemRepository: GradingSystemRepositoryInterface,
    private val boulderRepository: BoulderRepository
)  {

    fun getSessionsByGroup(groupId: Long, userId: Long): ServiceResult<ActiveSession> {
        try {
            val session = climbingSessionRepository.getActiveSession(groupId, userId)
            return ServiceResult(success = true, data = session, message = "Sessions fetched successfully")
        }  catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting sessions by group", data = null)
        }
    }

    fun uploadSession(userId: Long, climbingSession: ClimbingSessionDTO): ServiceResult<Unit> {
        try {
            val sessionId = climbingSessionRepository.uploadClimbingSession(climbingSession)
            //val rowAffected = climbingSessionRepository.insertRouteAttemptsInSession(climbingSession.routeAttempts, sessionId).reduce { acc, i -> acc + i }
            //if(rowAffected < climbingSession.routeAttempts.size) return ServiceResult(success = false, message = "Error uploading session", data = null)
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error uploading session", data = null)
        }
        return ServiceResult(success = true, message = "Session uploaded successfully")
    }

    fun openSession(groupId: Long, placeId: Long, userId: Long): ServiceResult<ActiveSession> {
        try {
            val activeSessionId = climbingSessionRepository.openActiveSession(userId, groupId, placeId)
            val activeSession = climbingSessionRepository.getActiveSession(activeSessionId)?.run {
                climbingSessionRepository.getActiveSession(activeSessionId)
            } ?: run {
                climbingSessionRepository.deleteActiveSession(activeSessionId)
                return ServiceResult(success = false, message = "Something went wrong while fetching opened session", data = null)
            }
            return ServiceResult(success = true, data = activeSession, message = "Session opened successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error opening session; ${e.message}", data = null)
        }
    }

    fun closeSession(sessionId: Long, save: Boolean, userId: Long): ServiceResult<Unit> {
            // check if user is allowed
            //val activeSession = climbingSessionRepository.getActiveSession(sessionId) ?:
                //throw Exception("Session with ID $sessionId not found, cannot close it.")
        val session = climbingSessionRepository.getSessionById(sessionId) ?:
            throw Exception("Session with ID $sessionId not found, cannot close it.")
        session.userId == userId || throw Exception("User with ID $userId has no access to this session.")
        if(save) {
            val rowsAffected = climbingSessionRepository.setSessionAsInactive(sessionId)
            if(rowsAffected <= 0) throw Exception("Something went wrong while saving session")
            return ServiceResult(success = true, message = "Session saved successfully")
        }
        val rowsAffected = climbingSessionRepository.deleteActiveSession(sessionId)
        if(rowsAffected <= 0) throw Exception("Session with ID $sessionId not found, cannot close it.")
        return ServiceResult(success = true, message = "Session closed successfully")
    }
    fun addRouteAttempt(activeSessionId: Long, userId: Long, routeAttempt: RouteAttemptDTO): ServiceResult<RouteAttemptDisplay> {
        val activeSession = climbingSessionRepository.getActiveSession(activeSessionId) ?:
            throw Exception("Session with ID $activeSessionId not found, cannot add route attempt.")
        activeSession.userId == userId || throw Exception("User with ID $userId has no access to this session.")
        val insertedAttempt = climbingSessionRepository.addRouteAttemptToActiveSession(activeSessionId, routeAttempt)
        val attemptDisplay = routeAttemptToDisplay(insertedAttempt)
        return ServiceResult(success = true, message = "Test", data = attemptDisplay)
    }

    fun removeRouteAttempt(attemptId: Long, userId: Long): ServiceResult<Unit> {
        try {
            val rowsAffected = climbingSessionRepository.deleteRouteAttempt(attemptId)
            if(rowsAffected <= 0) throw Exception("Route attempt with ID $attemptId not found, cannot remove it.")
            return ServiceResult(success = true, message = "Route attempt removed successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error removing route attempt: ${e.message}", data = null)
        }
    }

    fun getRouteAttempts(sessionId: Long): ServiceResult<List<RouteAttemptDisplay>> {
        try {
            //val session = climbingSessionRepository.getActiveSession(sessionId) ?: throw Exception("Session has not been opened yet")
            val routeAttempts = climbingSessionRepository.getRouteAttemptsBySessionId(sessionId)
            val routeDisplays = routeAttempts.map {
                return@map routeAttemptToDisplay(it)
            }
            return ServiceResult(success = true, data = routeDisplays, message = "Route attempts fetched successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting route attempts: ${e.message}", data = null)
        }
    }

    fun updateRouteAttempt(userId: Long, routeAttempt: UpdateAttemptRequest): ServiceResult<Unit> {
        val oldRouteAttempt = climbingSessionRepository.getRouteAttemptById(routeAttempt.id)
        val rowAffected = climbingSessionRepository.updateRouteAttempt(routeAttempt)
        if(rowAffected <= 0) throw Exception("Route attempt with ID ${routeAttempt.id} not found, cannot update it.")
        return ServiceResult(success = true, message = "Route attempt updated successfully")
    }


    fun routeAttemptToDisplay(routeAttempt: RouteAttempt): RouteAttemptDisplay {
        val session = climbingSessionRepository.getActiveSession(routeAttempt.session) ?: throw Exception("Session has not been opened yet")
        val place = placeRepository.getPlaceById(session.placeId) ?: throw Exception("Session has not a valid place")
        val grades = gradingSystemRepository.getGradesBySystemId(place.gradingSystem)
        val route = boulderRepository.getRouteById(routeAttempt.routeId) ?: throw Exception("Route not found")
        val gradeName = grades.find { grade -> grade.id == route.grade }?.gradeString ?: throw Exception("Grade not found")
        return RouteAttemptDisplay(routeAttempt.id, routeAttempt.attempts, routeAttempt.completed, route.name, gradeName, routeAttempt.timestamp)
    }
}