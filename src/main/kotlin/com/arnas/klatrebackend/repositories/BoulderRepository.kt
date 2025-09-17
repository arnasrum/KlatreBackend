package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.Boulder
import com.arnas.klatrebackend.dataclasses.BoulderRequest
import com.arnas.klatrebackend.interfaces.repositories.BoulderRepositoryInterface
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder

@Repository
class BoulderRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
): BoulderRepositoryInterface {

    override fun addBoulder(userId: Long, boulder: BoulderRequest): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()

        val updates = mutableListOf<String>()
        val values = mutableListOf<String>()
        val parameters = MapSqlParameterSource()

        updates.add("name")
        values.add(":name")
        parameters.addValue("name", boulder.name)

        updates.add("grade")
        values.add(":grade")
        parameters.addValue("grade", boulder.grade)

        updates.add("place")
        values.add(":place")
        parameters.addValue("place", boulder.place)

        if(boulder.description != null) {
            updates.add("description")
            values.add(":description")
            parameters.addValue("description", boulder.description)
        }

        val sql = "INSERT INTO boulders (${updates.joinToString(", ")}) VALUES (${values.joinToString(", ")})"

        jdbcTemplate.update(sql, parameters, keyHolder)
        val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

override fun updateBoulder(boulderId: Long, name: String?, grade: Long?, place: Long?, description: String?): Int {

    val updates = mutableListOf<String>()
    val parameters = mutableMapOf<String, Any>("boulderID" to boulderId)

    if (!name.isNullOrEmpty()) {
        updates.add("name = :name")
        parameters["name"] = name
    }
    if (grade != null) {
        updates.add("grade = :grade")
        parameters["grade"] = grade
    }
    if (place != null) {
        updates.add("place = :place")
        parameters["place"] = place
    }

    if (!description.isNullOrEmpty()) {
        updates.add("description = :description")
        parameters["description"] = description
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
        val sql = "SELECT b.id, b.name, b.description, g.id AS gId, b.place " +
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
                    grade = rs.getLong("gId"),
                    place = rs.getLong("place"),
                    image = null
                )
            )
        }
        return boulders
    }
}