package com.arnas.klatrebackend.repositories

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class StatRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    fun getUserTotalAttempts(userId: Long, groupId: Long): Int? {
        val sql = """
            SELECT
                u.name,
                COUNT(rs.id) AS total_sends
            FROM
                users u
            JOIN
                user_groups ug ON u.id = ug.user_id
            JOIN
                klatre_groups kg ON ug.group_id = kg.id
            JOIN
                route_sends rs ON u.id = rs.userID
            WHERE
                kg.id = :groudId AND u.id = :userId
            GROUP BY
                u.id
            ORDER BY
                total_sends DESC;
        """.trimIndent()
        val totalAttempts = jdbcTemplate.query(sql, mapOf("userId" to userId, "groupId" to groupId)) { rs, _ ->
            rs.getInt("total_sends")
        }
        return totalAttempts.firstOrNull()
    }

    fun getUserHardestSend(userId: Long, groupId: Long): Long? {
        val sql = """
            SELECT
                u.name,
                b.id AS routeId,
                MAX(g.numerical_value) AS hardest_send_value
            FROM
                users u
            JOIN
                user_groups ug ON u.id = ug.user_id
            JOIN
                klatre_groups kg ON ug.group_id = kg.id
            JOIN
                route_sends rs ON u.id = rs.userID
            JOIN
                boulders b ON rs.boulderID = b.id
            JOIN
                grades g ON b.grade = g.id
            WHERE
                rs.completed = TRUE AND kg.id = :groudId AND u.id = :userId 
            GROUP BY
                u.id
            ORDER BY
                hardest_send_value DESC;
        """.trimIndent()
        val hardestSend = jdbcTemplate.query(sql, mapOf("userId" to userId, "groupId" to groupId)) { rs, _ ->
            rs.getLong("routeId")
        }
        return hardestSend.firstOrNull()
    }

    fun getUserTotalCompletedRoutes(userId: Long, groupId: Long): Int? {
        val sql = """
            SELECT
                kg.name AS group_name,
                COUNT(rs.id) AS total_completed_sends
            FROM
                klatre_groups kg
            JOIN
                user_groups ug ON kg.id = ug.group_id
            JOIN
                route_sends rs ON ug.user_id = rs.userID
            WHERE
                rs.completed = TRUE AND kg.id = :groupId AND ug.user_id = :userId 
            GROUP BY
                kg.id;
        """.trimIndent()

        val totalCompleted = jdbcTemplate.query(sql, mapOf("userId" to userId, "groupId" to groupId)) { rs, _ ->
            rs.getInt("total_completed_routes")
        }
        return totalCompleted.firstOrNull()
    }

    fun getGroupTotalAttempts(groupId: Long): Int? {
        val sql = """
            SELECT
                kg.name AS group_name,
                SUM(rs.attempts) AS total_attempts
            FROM
                klatre_groups kg
            JOIN
                user_groups ug ON kg.id = ug.group_id
            JOIN
                route_sends rs ON ug.user_id = rs.userID
            WHERE
                kg.id = :groupId
            GROUP BY
                kg.id;
        """
        val totalAttempts = jdbcTemplate.query(sql, mapOf("groupId" to groupId)) { rs, _ ->
            rs.getInt("total_completed_routes")
        }
        return totalAttempts.firstOrNull()
    }

    fun getGroupTotalCompletedRoutes(groupId: Long): Int? {
        val sql = """
            SELECT
                kg.name AS group_name,
                COUNT(rs.id) AS total_completed_sends
            FROM
                klatre_groups kg
            JOIN
                user_groups ug ON kg.id = ug.group_id
            JOIN
                route_sends rs ON ug.user_id = rs.userID
            WHERE
                kg.name = 'Climbing Buddies' AND rs.completed = TRUE -- Replace with your desired group name
            GROUP BY
                kg.id;
        """.trimIndent()
        val totalAttempts = jdbcTemplate.query(sql, mapOf("groupId" to groupId)) { rs, _ ->
            rs.getInt("total_completed_routes")
        }
        return totalAttempts.firstOrNull()
    }

    fun getGroupHardestRouteCompleted(groupId: Long): Long? {
        val sql = """
            SELECT
                kg.name AS group_name,
                b.id AS routeId,
                g.grade_string AS hardest_completed_grade,
                g.numerical_value AS grade_value
            FROM
                klatre_groups kg
            JOIN
                user_groups ug ON kg.id = ug.group_id
            JOIN
                route_sends rs ON ug.user_id = rs.userID
            JOIN
                boulders b ON rs.boulderID = b.id
            JOIN
                grades g ON b.grade = g.id
            WHERE
                kg.id = :groupId AND rs.completed = TRUE 
            ORDER BY
                g.numerical_value DESC
            LIMIT 1;
        """.trimIndent()
        val hardestSend = jdbcTemplate.query(sql, mapOf("groupId" to groupId)) { rs, _ ->
            rs.getLong("routeId")
        }
        return hardestSend.firstOrNull()
    }

    fun groupActivityOverTime(groupId: Long, timeAggregate: String) {
        val sql = """
            SELECT
            DATE_TRUNC(:timeAggregate, rs.date) AS activity_period,
            COUNT(rs.id) AS completed_sends
            FROM
            klatre_groups kg
                    JOIN
            user_groups ug ON kg.id = ug.group_id
                    JOIN
            route_sends rs ON ug.user_id = rs.userID
                    WHERE
            kg.id = :groupId AND rs.completed = TRUE 
            GROUP BY
                    activity_period
            ORDER BY
                    activity_period;
        """
        val timeData = jdbcTemplate.query(sql,
            mapOf("groupId" to groupId, "timeAggregate" to timeAggregate)) { rs, _ ->
            rs.getString("activity_period")
        }
        println("timeData: $timeData")
    }




}