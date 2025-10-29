package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import com.arnas.klatrebackend.util.toSnakeCase
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
                groupId = rs.getLong("group_id"),
                gradingSystemId = rs.getLong("grading_system_id")
            )
        }
        val places =  jdbcTemplate.query("SELECT * FROM places WHERE group_id = :groupId",
            mapOf("groupId" to groupId),
            rowMapper
        )
        return places.toList()
    }

    override fun addPlaceToGroup(groupID: Long, name: String, description: String?): Long {
        val keyholder = GeneratedKeyHolder()
        val tableColumns = mutableListOf<String>("name", "group_id")
        val placeHolders = mutableListOf<String>(":name", ":groupID")
        val parameters = MapSqlParameterSource()
        parameters.addValue("name", name)
        parameters.addValue("groupID", groupID)
        description?.let {
            tableColumns.add("description")
            placeHolders.add(":description")
            parameters.addValue("description", it)
        }

        val sql = "INSERT INTO places (${tableColumns.joinToString(", ")}) VALUES (${placeHolders.joinToString(", ")})"
        println(sql)
        jdbcTemplate.update(
            sql, parameters, keyholder
        )
        val keys = keyholder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

    override fun getPlaceById(placeId: Long): Place?  {
        val results = jdbcTemplate.query("SELECT * FROM places WHERE id = :placeId",
            MapSqlParameterSource().addValue("placeId", placeId)
        ) { rs, _ ->
            Place(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                groupId = rs.getLong("group_id"),
                gradingSystemId = rs.getLong("grading_system_id")
            )
        }
        return results.firstOrNull()
    }

    override fun updatePlace(newPlace: Place): Int {
        val updates = mutableListOf<String>()
        val parameters = MapSqlParameterSource().addValue("placeId", newPlace.id)
        Place::class.memberProperties.forEach { prop ->
            if(prop.getter.call(newPlace) == null || prop.name == "id") return@forEach
            updates.add("${prop.name.toSnakeCase()} = :${prop.name}")
            parameters.addValue(prop.name, prop.getter.call(newPlace))
        }
        val sql = "UPDATE places SET ${updates.joinToString(", ")} WHERE id = :placeId"
        val rowAffected = jdbcTemplate.update(sql, parameters)
        return rowAffected
    }

    override fun deletePlace(placeId: Long): Int {
        val rowAffected = jdbcTemplate.update("DELETE FROM places WHERE id=:placeId",
            MapSqlParameterSource()
                .addValue("placeId", placeId)
        )
        return rowAffected
    }

}