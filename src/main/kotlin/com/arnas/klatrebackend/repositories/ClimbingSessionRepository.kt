package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.ClimbingSession
import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.RouteAttempt
import com.arnas.klatrebackend.dataclasses.RouteSend
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

@Repository
class ClimbingSessionRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    // Flexible date formatter that accepts both "2025-10-4" and "2025-10-04"
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-M-d")
        .toFormatter()

    fun getClimbingSessionByGroupId(groupId: Long, userId: Long): List<ClimbingSession> {

        val sql = "SELECT * FROM climbing_sessions WHERE group_id = :groupId AND user_id = :userId ORDER BY start_date"
        val params = MapSqlParameterSource()
            .addValue("groupId", groupId)
            .addValue("userId", userId)

        val climbingSessions = jdbcTemplate.query(sql, params) { rs, _ ->
            ClimbingSession(
                id = rs.getLong("id"),
                groupId = rs.getLong("group_id"),
                userId = rs.getLong("user_id"),
                placeId = rs.getLong("place_id"),
                startDate = rs.getString("start_date"),
                //name = rs.getString("name"),
                name = "nameless",
                routeAttempts = getRouteSendsBySessionId(rs.getLong("id")),
            )
        }
        return climbingSessions
    }

    fun uploadClimbingSession(climbingSession: ClimbingSessionDTO): Long {
        val columnMappings = mutableMapOf<String, Any?>()
        val keyHolder = GeneratedKeyHolder()

        climbingSession.name.let { columnMappings["name"] = it }
        climbingSession.userId.let { columnMappings["user_id"] = it }
        climbingSession.startDate.let {
            columnMappings["start_date"] = LocalDate.parse(it, dateFormatter)
        }
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

    fun getRouteSendsBySessionId(sessionId: Long): List<RouteAttempt> {
        val sql = "SELECT * FROM route_sends WHERE climbingsession = :sessionId"
        val params = MapSqlParameterSource()
            .addValue("sessionId", sessionId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            RouteAttempt(
                routeId = rs.getLong("boulderID"),
                attempts = rs.getInt("attempts"),
                completed = rs.getBoolean("completed"),
            )
        }
    }

    fun insertRouteAttemptsInSession(routeAttempts: List<RouteAttempt>, sessionId: Long): IntArray {
        return jdbcTemplate.batchUpdate(
            "INSERT INTO route_sends (boulderid, climbingsession, attempts, completed) VALUES (:routeId, :sessionId, :attempts, :completed)",
            routeAttempts.map {
                mapOf(
                    "routeId" to it.routeId,
                    "sessionId" to sessionId,
                    "completed" to it.completed,
                    "attempts" to it.attempts
                )
            }.toTypedArray()
        )
    }

}