package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.BoulderRequest
import com.arnas.klatrebackend.dataclass.RouteSend
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import kotlin.reflect.full.memberProperties

@Repository
class BoulderRepository(
    private var jdbcTemplate: NamedParameterJdbcTemplate
) {


    fun addBoulder(userId: Long, boulder: BoulderRequest): Long {
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

fun updateBoulder(boulderInfo: Map<String, String>): Long {
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
    jdbcTemplate.update(sql, parameters)

    return 1
}

    open fun deleteBoulder(boulderID: Long): Boolean {
        jdbcTemplate.update("DELETE FROM boulders WHERE id=:boulderID",
            MapSqlParameterSource()
                .addValue("boulderID", boulderID)
        )
        return true
    }

    open fun getBouldersByPlace(placeID: Long): Array<Boulder> {
        val boulders: MutableList<Boulder> = mutableListOf()
        val sql = "SELECT b.id, b.name, b.description, g.grade_string, b.place " +
                "FROM boulders AS b INNER JOIN places AS p ON b.place = p.id " +
                "INNER JOIN grades AS g ON g.system_id = p.grading_system_id " +
                "WHERE b.place=:placeID AND g.id = b.grade " +
                "ORDER BY b.id"
        jdbcTemplate.query(
            sql,
            MapSqlParameterSource()
                .addValue("placeID", placeID)
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
        return boulders.toTypedArray()
    }

    open fun getBoulderByPlace(placeID: List<Long>): List<Boulder> {
        val boulders: MutableList<Boulder> = mutableListOf()
        jdbcTemplate.query("SELECT * FROM boulders WHERE place IN (:placeID) ORDER BY id",
            MapSqlParameterSource()
                .addValue("placeID", placeID)
        ) { rs, _ ->
            boulders.add(
                Boulder(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    grade = rs.getString("grade"),
                    place = rs.getLong("place"),
                    description = rs.getString("description"),
                    image = null,
                )
            )
        }
        return boulders.toList()
    }




    open fun getBoulderSends(userID: Long, boulderIDs: List<Long>): List<RouteSend> {
        val routeSends: MutableList<RouteSend> = mutableListOf()
        jdbcTemplate.query("SELECT * FROM route_sends WHERE userID=:userID AND boulderID IN (:boulderIDs)",
            MapSqlParameterSource()
                .addValue("userID", userID)
                .addValue("boulderIDs", boulderIDs)
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

    open fun insertRouteSend(userID: Long, boulderID: Long, sendInfo: Map<String, String>): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()

        val columns = mutableListOf("userID", "boulderID")
        val values = mutableListOf(":userID", ":boulderID")
        val parameters = MapSqlParameterSource()
            .addValue("userID", userID)
            .addValue("boulderID", boulderID)

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