package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BoulderRepository(private var userRepository: UserRepository,
                        private var jdbcTemplate: NamedParameterJdbcTemplate) {


    fun getBouldersByUser(user: User): List<Boulder> {
        //val bouldersMap = mutableMapOf<Int, Map<String, String>>()
        val boulders: MutableList<Boulder> = mutableListOf()
        val userID = userRepository.getUserIDByObject(user)
        jdbcTemplate.query("SELECT * FROM boulders WHERE userID=:userID ORDER BY id",
            MapSqlParameterSource()
                .addValue("userID", userID),
            RowMapper { rs, _ ->
                var i = 1
                do {
                    boulders.add(
                        Boulder(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getInt("attempts"),
                            rs.getString("grade"),
                            null
                        ))
                } while (rs.next())
            })
            return boulders.toList()
    }

    fun addBoulder(userId: Int, boulderInfo: Map<String, String>): Boolean {
        jdbcTemplate.update("INSERT INTO boulders (name, attempts, grade, image, userID)" +
                    " VALUES (:name, :attempts, :grade, :image, :userID)",
            MapSqlParameterSource()
                .addValue("name", boulderInfo["name"])
                .addValue("attempts", boulderInfo["attempts"]?.toInt())
                .addValue("grade", boulderInfo["grade"])
                .addValue("userID", userId)
        )
        return true
    }


    fun updateBoulder(boulderID: Int, boulderInfo: Map<String, String>): Boolean {
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


}