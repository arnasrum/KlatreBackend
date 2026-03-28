package com.arnas.klatrebackend.features.climbingsessions

interface ClimbingSessionRepository {
    fun getClimbingSessionById(sessionId: Long): ClimbingSession?
    fun getActiveSession(groupId: Long, userId: Long): ClimbingSession?
    fun getPastSessions(groupId: Long, userId: Long): List<ClimbingSession>
    fun openActiveSession(userId: Long, groupId: Long, placeId: Long): Long
    fun setSessionAsInactive(activeSessionId: Long): Int
    fun uploadClimbingSession(climbingSession: ClimbingSessionDTO): Long
    fun deleteClimbingSession(climbingSessionId: Long): Int
    fun getRouteAttemptsBySessionId(sessionId: Long): List<RouteAttempt>
    fun updateRouteAttempt(routeAttempt: RouteAttempt): Int
    fun deleteRouteAttempt(routeAttemptId: Long): Int
    fun addRouteAttemptToActiveSession(activeSessionId: Long, routeAttempt: RouteAttemptDTO): RouteAttempt
    fun getRouteAttemptById(id: Long): RouteAttempt
}

