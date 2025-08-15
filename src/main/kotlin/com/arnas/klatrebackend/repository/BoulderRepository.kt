package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.BoulderRequest
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import kotlin.reflect.full.memberProperties

@Repository
class BoulderRepository(private var userRepository: UserRepository,
                        private var jdbcTemplate: NamedParameterJdbcTemplate) {


    fun getBouldersByUser(userID: Long): List<Boulder> {
        val boulders: MutableList<Boulder> = mutableListOf()
        jdbcTemplate.query("SELECT * FROM boulders WHERE userID=:userID ORDER BY id",
            MapSqlParameterSource()
                .addValue("userID", userID),
            RowMapper { rs, _ ->
                do {
                    boulders.add(
                        Boulder(
                            id = rs.getLong("id"),
                            name = rs.getString("name"),
                            description = rs.getString("description"),
                            grade = rs.getString("grade"),
                            place = rs.getLong("place"),
                            image = null
                        ))
                } while (rs.next())
            })
            return boulders.toList()
    }

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

    val boulderProperties = Boulder::class.memberProperties
        .filter { it.returnType.classifier == String::class }
        .map { it.name }
    println("boulder properties: $boulderProperties")

    val fieldConverters: Map<String, (String) -> Any> = boulderProperties.associateWith {
        { value: String -> value }
    }

    val (setClauses, parameters) = fieldConverters.entries.fold(
        mutableListOf<String>() to MapSqlParameterSource().addValue("boulderID", boulderInfo["boulderID"]?.toLong())
    ) { (clauses, params), (field, converter) ->
        boulderInfo[field]?.let { value ->
            clauses += ("$field = :$field")
            params.addValue(field, converter(value))
        }
        clauses to params
    }
    
    if (setClauses.isEmpty()) return 0
    
    val sql = "UPDATE boulders SET ${setClauses.joinToString(", ")} WHERE id = :boulderID"
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
        jdbcTemplate.query("SELECT * FROM boulders WHERE place=:placeID ORDER BY id",
            MapSqlParameterSource()
                .addValue("placeID", placeID)
        ) { rs, _ ->
            boulders.add(
                Boulder(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    grade = rs.getString("grade"),
                    place = rs.getLong("place"),
                    image = null
                )
            )
        }
        return boulders.toTypedArray()
    }
}