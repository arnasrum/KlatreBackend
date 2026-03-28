package com.arnas.klatrebackend.features.climbingsessions

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
class ClimbingSessionRepositoryDefault(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : ClimbingSessionRepository {

    override fun getClimbingSessionById(sessionId: Long): ClimbingSession? {
        val sql = "SELECT * FROM climbing_sessions WHERE id = :id"
        val parameters = MapSqlParameterSource().addValue("id", sessionId)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            ClimbingSession(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                groupId = rs.getLong("group_id"),
                placeId = rs.getLong("place_id"),
                timestamp = rs.getLong("created_at"),
                active = rs.getBoolean("active"),
                routeAttempts = getRouteAttemptsBySessionId(sessionId)
            )
        }.firstOrNull()
    }

    override fun getActiveSession(groupId: Long, userId: Long): ClimbingSession? {
        val sql = "SELECT * FROM climbing_sessions WHERE group_id = :groupId AND user_id = :userId AND active = :active"
        val parameters = MapSqlParameterSource()
            .addValue("groupId", groupId)
            .addValue("userId", userId)
            .addValue("active", true)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            ClimbingSession(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                groupId = rs.getLong("group_id"),
                placeId = rs.getLong("place_id"),
                timestamp = rs.getLong("created_at"),
                active = rs.getBoolean("active"),
                routeAttempts = getRouteAttemptsBySessionId(rs.getLong("id"))
            )
        }.firstOrNull()
    }

    override fun getPastSessions(groupId: Long, userId: Long): List<ClimbingSession> {
        val sql = "SELECT * FROM climbing_sessions WHERE group_id = :groupId AND user_id = :userId AND active = :active"
        val parameters = MapSqlParameterSource()
            .addValue("groupId", groupId)
            .addValue("userId", userId)
            .addValue("active", false)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            ClimbingSession(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                groupId = rs.getLong("group_id"),
                placeId = rs.getLong("place_id"),
                timestamp = rs.getLong("created_at"),
                active = rs.getBoolean("active"),
                routeAttempts = getRouteAttemptsBySessionId(rs.getLong("id"))
            )
        }
    }

    override fun openActiveSession(userId: Long, groupId: Long, placeId: Long): Long {
        val sql = "INSERT INTO climbing_sessions(user_id, group_id, place_id, active) VALUES (:userId, :groupId, :placeId, :active)"
        val parameters = MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("groupId", groupId)
            .addValue("placeId", placeId)
            .addValue("active", true)
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(sql, parameters, keyHolder)
        return keyHolder.keys!!["id"].toString().toLong()
    }

    override fun setSessionAsInactive(activeSessionId: Long): Int {
        val sql = "UPDATE climbing_sessions SET active = :active WHERE id = :activeSessionId"
        val parameters = MapSqlParameterSource()
            .addValue("active", false)
            .addValue("activeSessionId", activeSessionId)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun uploadClimbingSession(climbingSession: ClimbingSessionDTO): Long {
        val sql = "INSERT INTO climbing_sessions(user_id, group_id, place_id, active) VALUES (:userId, :groupId, :placeId, :active)"
        val parameters = MapSqlParameterSource()
            .addValue("user_id", climbingSession.userId)
            .addValue("group_id", climbingSession.groupId)
            .addValue("place_id", climbingSession.placeId)
            .addValue("active", false)
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(sql, parameters, keyHolder)
        return keyHolder.keys!!["id"].toString().toLong()
    }

    override fun deleteClimbingSession(climbingSessionId: Long): Int {
        val sql = "DELETE FROM climbing_sessions WHERE id = :climbingSessionId"
        val parameters = MapSqlParameterSource().addValue("climbingSessionId", climbingSessionId)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun getRouteAttemptsBySessionId(sessionId: Long): List<RouteAttempt> {
        val sql = "SELECT * FROM route_attempts WHERE session = :sessionId"
        val parameters = MapSqlParameterSource().addValue("sessionId", sessionId)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            RouteAttempt(
                id = rs.getLong("id"),
                attempts = rs.getInt("attempts"),
                completed = rs.getBoolean("completed"),
                routeId = rs.getLong("route_id"),
                timestamp = rs.getLong("last_updated"),
                session = rs.getLong("session")
            )
        }
    }

    override fun updateRouteAttempt(routeAttempt: RouteAttempt): Int {
        val sql = "UPDATE route_attempts SET attempts = :attempts, completed = :completed, last_updated = :lastUpdated WHERE id = :id"
        val parameters = MapSqlParameterSource()
            .addValue("attempts", routeAttempt.attempts)
            .addValue("completed", routeAttempt.completed)
            .addValue("lastUpdated", Date().time)
            .addValue("id", routeAttempt.id)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun deleteRouteAttempt(routeAttemptId: Long): Int {
        val sql = "DELETE FROM route_attempts WHERE id = :routeAttemptId"
        val parameters = MapSqlParameterSource().addValue("routeAttemptId", routeAttemptId)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun addRouteAttemptToActiveSession(activeSessionId: Long, routeAttempt: RouteAttemptDTO): RouteAttempt {
        val sql = "INSERT INTO route_attempts(route_id, session, attempts, completed, last_updated) VALUES (:routeId, :activeSessionId, :attempts, :completed, :timestamp)"
        val keyHolder = GeneratedKeyHolder()
        val parameters = MapSqlParameterSource()
            .addValue("routeId", routeAttempt.routeId)
            .addValue("activeSessionId", activeSessionId)
            .addValue("attempts", routeAttempt.attempts)
            .addValue("completed", routeAttempt.completed)
            .addValue("timestamp", routeAttempt.timestamp)
        jdbcTemplate.update(sql, parameters, keyHolder)
        val id = keyHolder.keys!!["id"] as Long
        return getRouteAttemptById(id)
    }

    override fun getRouteAttemptById(id: Long): RouteAttempt {
        val sql = "SELECT * FROM route_attempts WHERE id = :id"
        val parameters = MapSqlParameterSource().addValue("id", id)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            RouteAttempt(
                id = rs.getLong("id"),
                attempts = rs.getInt("attempts"),
                completed = rs.getBoolean("completed"),
                routeId = rs.getLong("route_id"),
                timestamp = rs.getLong("last_updated"),
                session = rs.getLong("session")
            )
        }.first()
    }
}

