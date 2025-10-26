package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.ActiveSession
import com.arnas.klatrebackend.dataclasses.ClimbingSession
import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.RouteAttempt
import com.arnas.klatrebackend.dataclasses.RouteAttemptDTO
import com.arnas.klatrebackend.dataclasses.UpdateAttemptRequest

interface ClimbingSessionRepositoryInterface {
    fun getSessionById(sessionId: Long): ActiveSession?
    fun getActiveSession(groupId: Long, userId: Long): ActiveSession?
    fun getPastSessions(groupId: Long, userId: Long): List<ClimbingSession>
    fun openActiveSession(userId: Long, groupId: Long, placeId: Long): Long
    fun getActiveSession(activeSessionId: Long): ActiveSession?
    fun setSessionAsInactive(activeSessionId: Long): Int
    fun uploadClimbingSession(climbingSession: ClimbingSessionDTO): Long
    fun deleteClimbingSession(climbingSessionId: Long): Int
    fun getRouteAttemptsBySessionId(sessionId: Long): List<RouteAttempt>
    fun updateRouteAttempt(routeAttempt: UpdateAttemptRequest): Int
    fun deleteRouteAttempt(routeAttemptId: Long): Int
    fun addRouteAttemptToActiveSession(activeSessionId: Long, routeAttempt: RouteAttemptDTO): RouteAttempt
}