package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.interfaces.repositories.RouteSendRepositoryInterface
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import kotlin.reflect.full.memberProperties

@Repository
class RouteSendRepository(
    private var jdbcTemplate: NamedParameterJdbcTemplate
): RouteSendRepositoryInterface {

    override fun getBoulderSends(userId: Long, boulderIds: List<Long>): List<RouteSend> {
        val routeSends: MutableList<RouteSend> = mutableListOf()
        jdbcTemplate.query("SELECT * FROM route_sends WHERE userID=:userID AND boulderID IN (:boulderIDs)",
            MapSqlParameterSource()
                .addValue("userID", userId)
                .addValue("boulderIDs", boulderIds)
        ) { rs, _ ->
            routeSends.add(RouteSend(
                id = rs.getLong("id"),
                userID = rs.getLong("userID"),
                boulderID = rs.getLong("boulderID"),
                attempts = rs.getInt("attempts"),
                completed = rs.getBoolean("completed"),
                perceivedGrade = rs.getString("perceivedGrade")
            ))
        }
        return routeSends.toList()
    }

    override fun insertRouteSend(userId: Long, boulderId: Long, sendInfo: Map<String, String>): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()

        val columns = mutableListOf("userID", "boulderID")
        val values = mutableListOf(":userID", ":boulderID")
        val parameters = MapSqlParameterSource()
            .addValue("userID", userId)
            .addValue("boulderID", boulderId)

        RouteSend::class.memberProperties.forEach { prop ->
            if(sendInfo.containsKey(prop.name)) {
                columns.add(prop.name)
                values.add(":${prop.name}")
                val value = sendInfo[prop.name]
                val convertedValue = when(prop.name) {
                    "attempts" -> value?.toInt() ?: 0
                    "completed" -> value?.toBoolean() ?: false
                    "perceivedGrade" -> value
                    else -> value
                }
                parameters.addValue(prop.name, convertedValue)
            }
        }

        val sql = "INSERT INTO route_sends (${columns.joinToString(", ")}) VALUES (${values.joinToString(", ")})"
        jdbcTemplate.update(sql, parameters ,keyHolder)

        val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }



}