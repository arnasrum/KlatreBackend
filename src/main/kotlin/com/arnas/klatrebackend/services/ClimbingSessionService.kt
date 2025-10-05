package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.ClimbingSession
import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.repositories.ClimbingSessionRepository
import org.springframework.stereotype.Service

@Service
class ClimbingSessionService(
    private val climbingSessionRepository: ClimbingSessionRepository
) {

    fun getSessionsByGroup(groupId: Long, userId: Long): ServiceResult<List<ClimbingSession>> {
        try {
            val sessions = climbingSessionRepository.getClimbingSessionByGroupId(groupId, userId)
            // Test
            return ServiceResult(success = true, data = sessions, message = "Sessions fetched successfully")
        }  catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting sessions by group", data = null)
        }
    }

    fun uploadSession(userId: Long, climbingSession: ClimbingSessionDTO): ServiceResult<Unit> {
        try {
            val sessionId = climbingSessionRepository.uploadClimbingSession(climbingSession)
            val rowAffected = climbingSessionRepository.insertRouteAttemptsInSession(climbingSession.routeAttempts, sessionId).reduce { acc, i -> acc + i }
            if(rowAffected < climbingSession.routeAttempts.size) return ServiceResult(success = false, message = "Error uploading session", data = null)
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error uploading session", data = null)
        }
        return ServiceResult(success = true, message = "Session uploaded successfully")
    }


}