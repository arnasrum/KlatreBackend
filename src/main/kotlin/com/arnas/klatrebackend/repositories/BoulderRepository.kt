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


    override fun getRouteById(routeId: Long): Boulder? {
        val boulder = jdbcTemplate.query("SELECT * FROM boulders WHERE id = :routeId",
            mapOf("routeId" to routeId)
        ) { rs, _ ->
            Boulder(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                grade = rs.getLong("grade"),
                place = rs.getLong("place"),
                active = rs.getBoolean("active"),
                image = null
            )
        }
        return boulder.firstOrNull()
    }



    override fun addBoulder(userId: Long, boulder: BoulderRequest): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()

        val sql = """
            INSERT INTO boulders (name, grade, place, description, userId)
            VALUES (:name, :grade, :place, :description, :userId)
        """

        val parameters = MapSqlParameterSource()
            .addValue("name", boulder.name)
            .addValue("grade", boulder.grade)
            .addValue("place", boulder.place)
            .addValue("description", boulder.description) // Handles null automatically
            .addValue("userId", userId)

        jdbcTemplate.update(sql, parameters, keyHolder)



        val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

override fun updateBoulder(boulderId: Long, name: String?, grade: Long?, place: Long?, description: String?, active: Boolean?): Int {

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
    if (active != null) {
        updates.add("active = :active")
        parameters["active"] = active
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


    override fun getBouldersByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Boulder> {

        val boulders: MutableList<Boulder> = mutableListOf()
        val sql = "SELECT b.id, b.name, b.description, g.id AS gId, b.place, b.active AS active " +
                "FROM boulders AS b INNER JOIN places AS p ON b.place = p.id " +
                "INNER JOIN grades AS g ON g.system_id = p.grading_system_id " +
                "WHERE b.place=:placeID AND g.id = b.grade " +
                "ORDER BY b.date_added DESC"
        if(pagingEnabled) sql.plus(" LIMIT $limit OFFSET ${(page-1)*limit}")

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
                    active = rs.getBoolean("active"),
                    image = null
                )
            )
        }
        return boulders
    }

    override fun getNumBouldersInPlace(placeId: Long, countActive: Boolean): Int {
        val sql = "SELECT COUNT(*) FROM boulders WHERE place=:placeID AND active=:active"
        val result = jdbcTemplate.queryForObject(
            sql,
            MapSqlParameterSource()
                .addValue("placeID", placeId)
                .addValue("active", countActive)
        ) { rs, _ ->
            rs.getInt(1)
        }
        return result?: 0
    }
}