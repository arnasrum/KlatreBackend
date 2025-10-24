package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.ActiveSession
import com.arnas.klatrebackend.dataclasses.ClimbingSession
import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.RouteAttempt
import com.arnas.klatrebackend.dataclasses.RouteAttemptDTO
import com.arnas.klatrebackend.dataclasses.UpdateAttemptRequest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.time.format.DateTimeFormatterBuilder

@Repository
class ClimbingSessionRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    // Flexible date formatter that accepts both "2025-10-4" and "2025-10-04"
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-M-d-h:m")
        .toFormatter()

    fun getActiveSession(groupId: Long, userId: Long): ActiveSession? {
        val sql = "SELECT * FROM climbing_sessions WHERE group_id = :groupId AND user_id = :userId AND active = true"
        val params = MapSqlParameterSource()
            .addValue("groupId", groupId)
            .addValue("userId", userId)
        val session = jdbcTemplate.query(sql, params) { rs, _ ->
            ActiveSession(
                id = rs.getLong("id"),
                groupId = rs.getLong("group_id"),
                placeId = rs.getLong("place_id"),
                userId = rs.getLong("user_id"),
            )
        }
        return session.firstOrNull()
    }

    fun getPastSessions(groupId: Long, userId: Long): List<ClimbingSession> {
        val sql = "SELECT * FROM climbing_sessions WHERE group_id = :groupId AND user_id = :userId AND active = false"
        val params = MapSqlParameterSource()
            .addValue("groupId", groupId)
            .addValue("userId", userId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            ClimbingSession(
                id = rs.getLong("id"),
                groupId = rs.getLong("group_id"),
                userId = rs.getLong("user_id"),
                placeId = rs.getLong("place_id"),
                timestamp = rs.getTimestamp("created_at").time,
                name = "",
                routeAttempts = getRouteAttemptsBySessionId(rs.getLong("id"))
            )
        }
    }

    fun uploadClimbingSession(climbingSession: ClimbingSessionDTO): Long {
        val columnMappings = mutableMapOf<String, Any?>()
        val keyHolder = GeneratedKeyHolder()

        climbingSession.userId.let { columnMappings["user_id"] = it }
        climbingSession.timestamp.let { columnMappings["timestamp"] = it }
        climbingSession.placeId.let { columnMappings["place_id"] = it }
        climbingSession.groupId.let { columnMappings["group_id"] = it }

        if (columnMappings.isEmpty()) {
            throw IllegalArgumentException("No valid fields to insert")
        }
        
        val columns = columnMappings.keys.joinToString(", ")
        val placeholders = columnMappings.keys.joinToString(", ") { ":$it" }
        val params = MapSqlParameterSource()
        columnMappings.forEach { (key, value) -> params.addValue(key, value) }
        val sql = "INSERT INTO climbing_sessions($columns) VALUES ($placeholders)"
        jdbcTemplate.update(sql, params, keyHolder)
        val insertedObject = keyHolder.keys
        return insertedObject?.get("id") as Long? ?: throw RuntimeException("Failed to retrieve generated key")
    }

    fun deleteClimbingSession(climbingSessionId: Long): Int {
        return jdbcTemplate.update(
            "DELETE FROM climbing_sessions WHERE id = :climbingSessionId",
            mapOf("climbingSessionId" to climbingSessionId)
        )
    }


    fun getSessionById(sessionId: Long): ActiveSession? {
        val sql = "SELECT * FROM climbing_sessions WHERE id = :id"
        val params = MapSqlParameterSource()
            .addValue("id", sessionId)
        val session = jdbcTemplate.query(sql, params) { rs, _ ->
            ActiveSession(
                id = rs.getLong("id"),
                groupId = rs.getLong("group_id"),
                userId = rs.getLong("user_id"),
                placeId = rs.getLong("place_id"),
            )
        }
        return session.firstOrNull()
    }

    fun openActiveSession(userId: Long, groupId: Long, placeId: Long): Long {
        val keyholder = GeneratedKeyHolder()
        val sql = "INSERT INTO climbing_sessions (user_id, group_id, place_id, active) VALUES (:userId, :groupId, :placeId, true)"
        val rowAffected = jdbcTemplate.update(
            sql,
            MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("groupId", groupId)
                .addValue("placeId", placeId)
            , keyholder
        )
        return keyholder.keys?.get("id") as Long? ?: throw RuntimeException("Failed to retrieve generated key")
    }

    fun getActiveSession(activeSessionId: Long): ActiveSession? {
        val sql = "SELECT * FROM climbing_sessions WHERE id = :id AND active = true"
        val params = MapSqlParameterSource()
            .addValue("id", activeSessionId)
        val session = jdbcTemplate.query(sql, params) { rs, _ ->
            ActiveSession(
                id = rs.getLong("id"),
                groupId = rs.getLong("group_id"),
                placeId = rs.getLong("place_id"),
                userId = rs.getLong("user_id"),
            )
        }
        return session.firstOrNull()
    }


    fun setSessionAsInactive(activeSessionId: Long): Int {
        return jdbcTemplate.update(
            "UPDATE climbing_sessions SET active = false WHERE id = :id",
            mapOf("id" to activeSessionId)
        )
    }

    fun deleteActiveSession(activeSessionId: Long): Int {
        return jdbcTemplate.update(
            "DELETE FROM climbing_sessions WHERE id = :id AND active = true",
            mapOf("id" to activeSessionId)
        )
    }

    fun addRouteAttemptToActiveSession(activeSessionId: Long, routeAttempt: RouteAttemptDTO): RouteAttempt {
        val keyholder = GeneratedKeyHolder()
        println("routeAttempt: $routeAttempt")
        try {

        jdbcTemplate.update(
            "INSERT INTO route_attempts (route_id, attempts, completed, session, last_updated) VALUES " +
                    "(:routeId, :attempts, :completed, :sessionId, :timestamp)",
            MapSqlParameterSource()
                .addValue("routeId", routeAttempt.routeId)
                .addValue("attempts", routeAttempt.attempts)
                .addValue("completed", routeAttempt.completed)
                .addValue("sessionId", activeSessionId)
                .addValue("timestamp", routeAttempt.timestamp),
            keyholder
            )
        val generatedId = keyholder.keys?.get("id") as? Long
            ?: throw IllegalStateException("Failed to retrieve generated key for routeAttempt")

        return RouteAttempt(
            id = generatedId,
            routeId = routeAttempt.routeId,
            attempts = routeAttempt.attempts,
            completed = routeAttempt.completed,
            timestamp = routeAttempt.timestamp,
            session = activeSessionId,
        )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun getRouteAttemptById(routeAttemptId: Long): RouteAttempt? {
        val sql = "SELECT * FROM route_attempts WHERE id = :id"
        val params = MapSqlParameterSource()
            .addValue("id", routeAttemptId)
        val routeAttempt = jdbcTemplate.query(sql, params) { rs, _ ->
            RouteAttempt(
                id = rs.getLong("id"),
                routeId = rs.getLong("route_id"),
                attempts = rs.getInt("attempts"),
                completed = rs.getBoolean("completed"),
                timestamp = rs.getLong("last_updated"),
                session = rs.getLong("session"),
            )
        }
        return routeAttempt.firstOrNull()
    }

    fun getRouteAttemptsBySessionId(sessionId: Long): List<RouteAttempt> {
        val sql = "SELECT * FROM route_attempts WHERE session = :sessionId"
        val params = MapSqlParameterSource()
            .addValue("sessionId", sessionId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            RouteAttempt(
                id = rs.getLong("id"),
                routeId = rs.getLong("route_id"),
                attempts = rs.getInt("attempts"),
                completed = rs.getBoolean("completed"),
                timestamp = rs.getLong("last_updated"),
                session = rs.getLong("session"),
            )
        }
    }

    fun updateRouteAttempt(routeAttempt: UpdateAttemptRequest): Int  {
        val sql = "UPDATE route_attempts SET attempts = :attempts, completed = :completed, last_updated = :timestamp WHERE id = :id"
        return jdbcTemplate.update(
            sql,
            MapSqlParameterSource()
                .addValue("id", routeAttempt.id)
                .addValue("attempts", routeAttempt.attempts)
                .addValue("completed", routeAttempt.completed)
                .addValue("timestamp", routeAttempt.timestamp)
        )
    }

    fun deleteRouteAttempt(routeAttemptId: Long): Int {
        return jdbcTemplate.update(
            "DELETE FROM route_attempts WHERE id = :routeAttemptId",
            mapOf("routeAttemptId" to routeAttemptId)
        )
    }
}