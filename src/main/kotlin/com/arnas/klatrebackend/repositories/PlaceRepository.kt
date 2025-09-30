package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO
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
                groupID = rs.getLong("group_id"),
                gradingSystem = rs.getLong("grading_system_id")
            )
        }
        return results.firstOrNull()
    }

    override fun updatePlace(placeUpdateDTO: PlaceUpdateDTO): Int {
        val updates = mutableListOf<String>()
        val parameters = MapSqlParameterSource().addValue("placeId", placeUpdateDTO.placeId)

        placeUpdateDTO.name?.let {
            updates.add("name = :name")
            parameters.addValue("name", it)
        }
        placeUpdateDTO.description?.let {
            updates.add("description = :description")
            parameters.addValue("description", it)
        }
        if(updates.isEmpty()) {return 0}
        val sql = "UPDATE places SET ${updates.joinToString(", ")} WHERE id = :placeId"
        val rowAffected = jdbcTemplate.update(sql, parameters)

        return rowAffected
    }



    override fun updatePlace(placeId: Long, name: String?, description: String?, groupId: Long?, gradingSystem: Long?): Int {

        val updates = mutableListOf<String>()
        val parameters = MapSqlParameterSource().addValue("placeId", placeId)

        if (!name.isNullOrEmpty()) {
            updates.add("name = :name")
            parameters.addValue("name", name)
        }
        if (!description.isNullOrEmpty()) {
            updates.add("description = :description")
            parameters.addValue("description", description)
        }
        if (groupId != null) {
            updates.add("group_id = :groupId")
            parameters.addValue("groupId", groupId)
        }
        if (gradingSystem != null) {
            updates.add("grading_system_id = :gradingSystem")
            parameters.addValue("gradingSystem", gradingSystem)
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