package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.interfaces.repositories.UserRepositoryInterface
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
): UserRepositoryInterface {

    override fun insertUser(email: String, name: String): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(
            "INSERT INTO USERS (email, name) VALUES (:email, :name)",
            MapSqlParameterSource()
                .addValue("email", email)
                .addValue("name", name),
            keyHolder
        )
        val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

    override fun getUserById(userId: Long): User? {
        val fetchedUser = jdbcTemplate.query(
            "SELECT id, email, name FROM USERS WHERE id=:userId",
            MapSqlParameterSource().addValue("userId", userId)
        ) { rs, _ ->
            User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("name")
            )
        }
        return fetchedUser.firstOrNull()
    }

    override fun getUserByEmail(email: String): User? {
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