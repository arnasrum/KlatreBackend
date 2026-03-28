package com.arnas.klatrebackend.features.stats

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class StatsRepositoryDefault(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : StatsRepository {

    override fun getUserAttemptActivity(userId: Long, groupId: Long): List<UserGroupSessionStats> {
        val sql = """
            SELECT
                DATE(to_timestamp(cs.created_at)) AS session_date,
                COUNT(DISTINCT cs.id) AS sessions_that_day,
                COUNT(ra.id) AS route_attempts,
                SUM(ra.attempts) AS total_tries,
                SUM(CASE WHEN ra.completed THEN 1 ELSE 0 END) AS sends
            FROM
                climbing_sessions cs
            JOIN
                route_attempts ra ON ra.session = cs.id
            WHERE
                cs.user_id = :userId
                AND cs.group_id = :groupId
                AND cs.active = false
            GROUP BY
                session_date
            ORDER BY
                session_date
        """.trimIndent()
        val parameters = MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("groupId", groupId)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            val dateParts = rs.getString("session_date").split("-")
            UserGroupSessionStats(
                year = dateParts[0].toInt(),
                month = dateParts[1].toInt(),
                day = dateParts[2].toInt(),
                routesTried = rs.getInt("route_attempts"),
                totalTries = rs.getInt("total_tries"),
                totalCompleted = rs.getInt("sends"),
                groupId = groupId,
                userId = userId
            )
        }
    }
}

