package com.arnas.klatrebackend.repositories

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ClimbingSessionRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    fun getClimbingSessionByGroupId(groupId: Long) {

        val sql = "SELECT * FROM climbing_session WHERE groupid = :groupId"

    }

    fun uploadClimbingSession() {
        val sql = "INSERT INTO climbing_session VALUES (:id, :groupId, :userId, :date, :place, :routeId)"

    }


}