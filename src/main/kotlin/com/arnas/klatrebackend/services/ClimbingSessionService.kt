package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.ClimbingSession
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.repositories.ClimbingSessionRepository
import org.springframework.stereotype.Service

@Service
class ClimbingSessionService(
    climbingSessionRepository: ClimbingSessionRepository
) {

    fun getSessionsByGroup(groupId: Long, userId: Long): ServiceResult<List<ClimbingSession>> {
        try {
            return ServiceResult(success = true, message = "Sessions fetched successfully")
        }  catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting sessions by group", data = null)
        }

    }

    fun uploadSession(userId: Long, climbingSession: ClimbingSession): ServiceResult<Unit> {
        try {
            climbingSessionRepository.
        } catch (e: Exception) {
            return ServiceResult(success = false, message = "Error uploading session", data = null)
        }
        return ServiceResult(success = true, message = "Session uploaded successfully")
    }


}