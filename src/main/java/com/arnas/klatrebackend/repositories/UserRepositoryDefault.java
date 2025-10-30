package com.arnas.klatrebackend.repositories;

import com.arnas.klatrebackend.dataclasses.User;
import com.arnas.klatrebackend.interfaces.repositories.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class UserRepositoryDefault implements UserRepositoryInterface {


    private final NamedParameterJdbcTemplate jdbcTemplate;
    public UserRepositoryDefault(@Autowired NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public long insertUser(@NonNull String email, @NonNull String name) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                "INSERT INTO users(email, name) VALUES (:email, :name)",
                new MapSqlParameterSource()
                        .addValue("email", email)
                        .addValue("name", name),
                keyHolder
        );
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public User getUserByEmail(@NonNull String email) {
        var sql = "SELECT * FROM users WHERE email = :email";
        return jdbcTemplate.query(sql, new MapSqlParameterSource().addValue("email", email),
            (rs, index) ->
                new User(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("name")
                )
        ).getFirst();
    }

    @Override
    public User getUserById(long userId) {
        var sql = "SELECT * FROM users WHERE id = :userId";
        return jdbcTemplate.query(sql, new MapSqlParameterSource().addValue("userId", userId),
            (rs, index) ->
                new User(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("name")
                )
        ).getFirst();
    }
}
