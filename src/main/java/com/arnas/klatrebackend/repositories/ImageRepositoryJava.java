package com.arnas.klatrebackend.repositories;

import com.arnas.klatrebackend.dataclasses.Image;
import com.arnas.klatrebackend.interfaces.repositories.ImageRepositoryInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Primary
@Repository
public class ImageRepositoryJava implements ImageRepositoryInterface {

    NamedParameterJdbcTemplate jdbcTemplate;

    public ImageRepositoryJava(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public @Nullable Image getImageById(@NotNull String id) {
        var sql = "SELECT * FROM images WHERE id = :id";
        var parameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.query(sql, parameters,
            (rs, index) -> new Image(
                rs.getString("id"),
                rs.getString("contentType"),
                rs.getLong("fileSize")
            )
        ).getFirst();
    }

    @Override
    public int deleteImage(@NotNull String id) {
        var sql = "DELETE FROM images WHERE id = :id";
        var parameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    @NotNull
    public String storeImageMetaData(@NotNull String contentType, long size, long userId) {
        var sql = "INSERT INTO images(id, contentType, fileSize) VALUES (:id, :contentType, :fileSize)";
        var keyholder = new GeneratedKeyHolder();
        var parameters = new MapSqlParameterSource()
                .addValue("id", userId)
                .addValue("contentType", contentType)
                .addValue("fileSize", size);
        jdbcTemplate.update(sql, parameters, keyholder);
        return Objects.requireNonNull(keyholder.getKey()).toString();
    }

    @Override
    @Nullable
    public Image getImageMetadata(@NotNull String id) {
        var sql = "SELECT * FROM images WHERE id = :id";
        var parameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.query(sql, parameters,
                (rs, index) -> new Image(
                        rs.getString("id"),
                        rs.getString("contentType"),
                        rs.getLong("fileSize")
                )
        ).getFirst();
    }
}
