package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.BoulderRequest
import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.interfaces.repositories.BoulderRepositoryInterface
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import kotlin.reflect.full.memberProperties

@Repository
class BoulderRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
): BoulderRepositoryInterface {

    override fun addBoulder(userId: Long, boulder: BoulderRequest): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        
        jdbcTemplate.update(
            "INSERT INTO boulders (name, grade, userID, place)" +
            " VALUES (:name, :grade, :userID, :place)",
            MapSqlParameterSource()
                .addValue("name", boulder.name)
                .addValue("grade", boulder.grade)
                .addValue("userID", userId)
                .addValue("place", boulder.place),
            keyHolder
        )
        
        val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

override fun updateBoulder(boulderInfo: Map<String, String>): Int {
    val boulderId = boulderInfo["boulderID"]?.toLong() ?: return 0

    val updates = mutableListOf<String>()
    val parameters = mutableMapOf<String, Any>("boulderID" to boulderId)

    if (boulderInfo.containsKey("name")) {
        updates.add("name = :name")
        parameters["name"] = boulderInfo["name"]!!
    }
    if (boulderInfo.containsKey("grade") && !boulderInfo["grade"].isNullOrEmpty()) {
        updates.add("grade = :grade")
        parameters["grade"] = boulderInfo["grade"]!!.toString()
    }
    if (boulderInfo.containsKey("place")) {
        updates.add("place = :place")
        parameters["place"] = boulderInfo["place"]!!.toLong() // Example of type conversion
    }

    if (boulderInfo.containsKey("description") && !boulderInfo["description"].isNullOrEmpty()) {
        val descriptionValue = boulderInfo["description"]!!
        updates.add("description = :description")
        parameters["description"] = descriptionValue
    }
    if (updates.isEmpty()) {
        return 0
    }
    val sql = "UPDATE boulders SET ${updates.joinToString(", ")} WHERE id = :boulderID"
    val rowsAffected = jdbcTemplate.update(sql, parameters)
    return rowsAffected
}

    override fun deleteBoulder(boulderId: Long): Int {
        val rowAffected = jdbcTemplate.update("DELETE FROM boulders WHERE id=:boulderID",
            MapSqlParameterSource()
                .addValue("boulderID", boulderId)
        )
        return rowAffected
    }

    override fun getBouldersByPlace(placeId: Long): List<Boulder> {
        val boulders: MutableList<Boulder> = mutableListOf()
        val sql = "SELECT b.id, b.name, b.description, g.grade_string, b.place " +
                "FROM boulders AS b INNER JOIN places AS p ON b.place = p.id " +
                "INNER JOIN grades AS g ON g.system_id = p.grading_system_id " +
                "WHERE b.place=:placeID AND g.id = b.grade " +
                "ORDER BY b.id"
        jdbcTemplate.query(
            sql,
            MapSqlParameterSource()
                .addValue("placeID", placeId)
        ) { rs, _ ->
            boulders.add(
                Boulder(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    grade = rs.getString("grade_string"),
                    place = rs.getLong("place"),
                    image = null
                )
            )
        }
        return boulders
    }
}