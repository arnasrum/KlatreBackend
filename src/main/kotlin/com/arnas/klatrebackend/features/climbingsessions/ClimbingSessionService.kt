package com.arnas.klatrebackend.features.climbingsessions

interface ClimbingSessionService {
    fun getActiveSessionByGroup(groupId: Long, userId: Long): ClimbingSession?
    fun getPastSessionsByGroup(groupId: Long, userId: Long): List<ClimbingSession>
    fun uploadSession(userId: Long, climbingSession: ClimbingSessionDTO)
    fun openSession(groupId: Long, placeId: Long, userId: Long): ClimbingSession
    fun closeSession(sessionId: Long, save: Boolean, userId: Long)
    fun addRouteAttempt(activeSessionId: Long, userId: Long, routeAttempt: RouteAttemptDTO): RouteAttemptDisplay
    fun removeRouteAttempt(attemptId: Long, sessionId: Long, userId: Long)
    fun getRouteAttemptById(attemptId: Long): RouteAttempt
    fun getRouteAttempts(sessionId: Long): List<RouteAttemptDisplay>
    fun updateRouteAttempt(userId: Long, routeAttempt: RouteAttempt)
    fun getPastSessions(groupId: Long, userId: Long): List<ClimbingSessionDisplay>
}

