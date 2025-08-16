package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.User
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    open fun createUser(email: String, name: String): Long {
        // First check if user already exists
        val fetchedUser = jdbcTemplate.query(
            "SELECT id FROM USERS WHERE email=:email",
            MapSqlParameterSource().addValue("email", email)
        ) { rs, _ -> rs.getLong("id") }
        
        return if (fetchedUser.isNotEmpty()) {
            fetchedUser[0] // Return existing user's ID
        } else {
            // Use KeyHolder to get the generated ID
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            
            jdbcTemplate.update(
                "INSERT INTO USERS (email, name) VALUES (:email, :name)",
                MapSqlParameterSource()
                    .addValue("email", email)
                    .addValue("name", name),
                keyHolder
            )
            
            val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
            (keys["id"] as Number).toLong()
        }
    }

    open fun getUserIDByObject(user: User): Int {
        val userID = jdbcTemplate.query("SELECT id FROM USERS WHERE email=:email",
            MapSqlParameterSource()
                .addValue("email", user.email)
        ) { rs, _ -> rs.getInt("id") }
        return userID[0]
    }

    open fun getUserByEmail(email: String): User? {
        val fetchedUser = jdbcTemplate.query(
            "SELECT id, email, name FROM USERS WHERE email=:email",
            MapSqlParameterSource().addValue("email", email)
        ) { rs, _ ->
            User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("name")
            )
        }
        return fetchedUser.firstOrNull()
    }

}