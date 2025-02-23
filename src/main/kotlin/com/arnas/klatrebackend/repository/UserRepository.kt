package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun createUser(user: User): Boolean {

        val fetchedUser = jdbcTemplate.query("SELECT * FROM USERS WHERE email=:email",
            MapSqlParameterSource().addValue("email", user.email),
            { rs, _ -> rs.getInt("id") }
        )
        if (fetchedUser.isEmpty()) {
            jdbcTemplate.update("INSERT INTO USERS (email, name) VALUES (:email, :name)",
                MapSqlParameterSource()
                    .addValue("email", user.email)
                    .addValue("name", user.name)
            )
        }
        return true
    }
    fun getUserIDByObject(user: User): Int {
        val userID = jdbcTemplate.query("SELECT id FROM USERS WHERE email=:email",
            MapSqlParameterSource()
                .addValue("email", user.email)
        ) { rs, _ -> rs.getInt("id") }
        return userID[0]
    }
}