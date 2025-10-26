package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.ActiveSession
import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.ClimbingSessionDisplay
import com.arnas.klatrebackend.dataclasses.RouteAttempt
import com.arnas.klatrebackend.dataclasses.RouteAttemptDTO
import com.arnas.klatrebackend.dataclasses.RouteAttemptDisplay
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.dataclasses.UpdateAttemptRequest

interface ClimbingSessionServiceInterface {
    fun getSessionsByGroup(groupId: Long, userId: Long): ServiceResult<ActiveSession>
    fun uploadSession(userId: Long, climbingSession: ClimbingSessionDTO): ServiceResult<Unit>
    fun openSession(groupId: Long, placeId: Long, userId: Long): ServiceResult<ActiveSession>
    fun closeSession(sessionId: Long, save: Boolean, userId: Long): ServiceResult<Unit>
    fun addRouteAttempt(activeSessionId: Long, userId: Long, routeAttempt: RouteAttemptDTO): ServiceResult<RouteAttemptDisplay>
    fun removeRouteAttempt(attemptId: Long, userId: Long): ServiceResult<Unit>
    fun getRouteAttempts(sessionId: Long): ServiceResult<List<RouteAttemptDisplay>>
    fun updateRouteAttempt(userId: Long, routeAttempt: UpdateAttemptRequest): ServiceResult<Unit>
    fun routeAttemptToDisplay(routeAttempt: RouteAttempt): RouteAttemptDisplay
    fun getPastSessions(groupId: Long, userId: Long): List<ClimbingSessionDisplay>

}