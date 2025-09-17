package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import kotlin.reflect.full.memberProperties

@Repository
class PlaceRepository(
    private var jdbcTemplate: NamedParameterJdbcTemplate
): PlaceRepositoryInterface {

    override fun getPlacesByGroupId(groupId: Long): List<Place> {

        val rowMapper = RowMapper<Place> { rs: ResultSet, _: Int ->
            Place(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                groupID = rs.getLong("group_id"),
                gradingSystem = rs.getLong("grading_system_id")
            )
        }
        val places =  jdbcTemplate.query("SELECT * FROM places WHERE group_id = :groupId",
            mapOf("groupId" to groupId),
            rowMapper
        )
        return places.toList()
    }

    override fun addPlaceToGroup(groupID: Long, placeRequest: PlaceRequest): Long {
        val keyholder = GeneratedKeyHolder()

        val tableColumns = mutableListOf<String>()
        val placeHolders = mutableListOf<String>()
        val parameters = MapSqlParameterSource()
        placeRequest::class.memberProperties.forEach { prop ->
            val value = prop.getter.call(placeRequest)
            if(value != null) {
                parameters.addValue(prop.name, value)
                tableColumns.add(prop.name)
                placeHolders.add(":${prop.name}")
            }
        }
        val sql = "INSERT INTO places (${tableColumns.joinToString(", ")}) VALUES (${placeHolders.joinToString(", ")})"
        jdbcTemplate.update(
            sql, parameters, keyholder
        )
        val keys = keyholder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }
}