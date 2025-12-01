package com.arnas.klatrebackend.features.images;

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
public class ImageRepositoryJava implements ImageRepository {

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
                rs.getString("content_type"),
                rs.getLong("file_size")
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
        var sql = "INSERT INTO images(user_id, content_type, file_size) VALUES (:userId, :contentType, :fileSize)";
        var keyholder = new GeneratedKeyHolder();
        var parameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("contentType", contentType)
                .addValue("fileSize", size);
        jdbcTemplate.update(sql, parameters, keyholder);
        return Objects.requireNonNull(keyholder.getKeys().get("id")).toString();
    }

    @Override
    @Nullable
    public Image getImageMetadata(@NotNull String id) {
        var sql = "SELECT * FROM images WHERE id = :id";
        var parameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.query(sql, parameters,
                (rs, index) -> new Image(
                        rs.getString("id"),
                        rs.getString("content_type"),
                        rs.getLong("file_size")
                )
        ).getFirst();
    }
}
