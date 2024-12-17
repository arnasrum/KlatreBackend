package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BoulderRepository {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    fun getBouldersByUser(user: User): Map<Int, Map<String, String>> {
        val bouldersMap = mutableMapOf<Int, Map<String, String>>()
        val userID = userRepository.getUserIDByObject(user)
        jdbcTemplate.query("SELECT * FROM boulders WHERE userID=:userID",
            MapSqlParameterSource()
                .addValue("userID", userID),
            RowMapper { rs, _ ->
                do {
                    bouldersMap[rs.getInt("id")] = mapOf(
                        "attempts" to rs.getString("attempts"),
                        "grade" to rs.getString("grade"),
                        "image" to rs.getString("image")
                    )
                } while (rs.next())
            })
        return bouldersMap

    }
}