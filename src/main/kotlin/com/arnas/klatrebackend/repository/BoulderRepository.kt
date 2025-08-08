package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.BoulderRequest
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder

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
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getInt("attempts"),
                            rs.getString("grade"),
                            rs.getLong("place"),
                            null
                        ))
                } while (rs.next())
            })
            return boulders.toList()
    }

    fun addBoulder(userId: Long, boulder: BoulderRequest): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        
        jdbcTemplate.update(
            "INSERT INTO boulders (name, attempts, grade, userID, place)" +
            " VALUES (:name, :attempts, :grade, :userID, :place)",
            MapSqlParameterSource()
                .addValue("name", boulder.name)
                .addValue("attempts", boulder.attempts)
                .addValue("grade", boulder.grade)
                .addValue("userID", userId)
                .addValue("place", boulder.place),
            keyHolder
        )
        
        val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

    fun updateBoulder(boulderInfo: Map<String, String>): Boolean {
        jdbcTemplate.update(
            "UPDATE boulders " +
                    "SET attempts = :attempts, name = :name, grade = :grade" +
                    " WHERE id = :boulderID",
            MapSqlParameterSource()
                .addValue("boulderID", boulderInfo["id"]?.toInt())
                .addValue("attempts", boulderInfo["attempts"]?.toInt())
                .addValue("name", boulderInfo["name"])
                .addValue("grade", boulderInfo["grade"])
        )
        return true
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
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getInt("attempts"),
                    rs.getString("grade"),
                    rs.getLong("place"),
                    null
                )
            )
        }
        return boulders.toTypedArray()
    }
}